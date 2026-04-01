package com.yhdp.listener;

import cn.hutool.json.JSONUtil;
import com.yhdp.entity.VoucherOrder;
import com.yhdp.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OrderConsumer {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @KafkaListener(topics = "seckill.orders")
    public void onMessage(String message) {
        log.info("收到秒杀下单消息: {}", message);
        try {
            // 1.解析消息
            VoucherOrder voucherOrder = JSONUtil.toBean(message, VoucherOrder.class);
            // 2.创建订单
            voucherOrderService.createVoucherOrder(voucherOrder);
        } catch (Exception e) {
            log.error("处理秒杀下单消息异常", e);
        }
    }
}
