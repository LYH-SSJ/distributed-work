package com.yhdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhdp.dto.Result;
import com.yhdp.entity.VoucherOrder;
import com.yhdp.mapper.VoucherOrderMapper;
import com.yhdp.service.ISeckillVoucherService;
import com.yhdp.service.IVoucherOrderService;
import com.yhdp.utils.SnowflakeIdWorker;
import com.yhdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        // 1.执行lua脚本
        long orderId = snowflakeIdWorker.nextId();
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)
        );
        int r = result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }

        // 3.有购买资格，把下单信息保存到Kafka
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        
        // 发送消息到Kafka
        kafkaTemplate.send("seckill.orders", JSONUtil.toJsonStr(voucherOrder));

        // 4.返回订单id
        return Result.ok(orderId);
    }

    @Override
    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();

        // 1.一人一单 (二次校验)
        int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            log.error("用户已经购买过了");
            return;
        }

        // 2.扣减库存 (分布式一致性：调用库存服务逻辑)
        boolean success = seckillVoucherService.deductStock(voucherId);
        if (!success) {
            log.error("库存不足");
            return;
        }

        // 3.创建订单
        save(voucherOrder);
    }

    @Override
    @Transactional
    public Result payOrder(Long orderId) {
        // 1.查询订单
        VoucherOrder order = getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        // 2.判断订单状态 (1为未支付)
        if (order.getStatus() != 1) {
            return Result.fail("订单状态异常，无法支付");
        }
        // 3.原子性更新订单状态为已支付 (2为已支付)
        boolean success = update()
                .set("status", 2)
                .set("pay_time", LocalDateTime.now())
                .eq("id", orderId)
                .eq("status", 1) // 保证幂等性与一致性的关键条件
                .update();

        if (!success) {
            return Result.fail("支付失败，订单已被处理或状态已变更");
        }
        return Result.ok();
    }

    @Override
    public Result queryByOrderId(Long orderId) {
        VoucherOrder order = getById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        return Result.ok(order);
    }

    @Override
    public Result queryByUserId(Long userId) {
        List<VoucherOrder> orders = query().eq("user_id", userId).list();
        return Result.ok(orders);
    }
}
