package com.yhdp.service;

import com.yhdp.dto.Result;
import com.yhdp.entity.VoucherOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    void createVoucherOrder(VoucherOrder voucherOrder);

    Result queryByOrderId(Long orderId);

    Result queryByUserId(Long userId);
    Result payOrder(Long orderId);
}
