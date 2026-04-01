package com.yhdp.service;

import com.yhdp.entity.SeckillVoucher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 服务类
 * </p>
 *
 */
public interface ISeckillVoucherService extends IService<SeckillVoucher> {

    boolean deductStock(Long voucherId);
}
