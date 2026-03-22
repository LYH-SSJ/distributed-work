package com.yhdp.controller;

import com.yhdp.dto.Result;
import com.yhdp.service.IVoucherOrderService;
import com.yhdp.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }

    @GetMapping("/{id}")
    public Result queryByOrderId(@PathVariable("id") Long orderId) {
        return voucherOrderService.queryByOrderId(orderId);
    }

    @GetMapping("/user/{userId}")
    public Result queryByUserId(@PathVariable("userId") Long userId) {
        return voucherOrderService.queryByUserId(userId);
    }

    @GetMapping("/my")
    public Result queryMyOrders() {
        Long userId = UserHolder.getUser().getId();
        return voucherOrderService.queryByUserId(userId);
    }
}
