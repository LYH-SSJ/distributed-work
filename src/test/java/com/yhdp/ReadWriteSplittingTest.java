package com.yhdp;

import com.yhdp.entity.Shop;
import com.yhdp.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
public class ReadWriteSplittingTest {

    @Resource
    private IShopService shopService;

    @Test
    public void testReadWriteSplit() {
        log.info("============== 测试开始: 读写分离 ==============");
        
        // 1. 测试读操作 (应该路由到 slave)
        log.info("--- 测试 @DS(\"slave\") 读操作 ---");
        Shop shop = shopService.getById(1L);
        if (shop != null) {
            log.info("查询到店铺: {}", shop.getName());
        }

        // 2. 测试写操作 (应该路由到 master)
        log.info("--- 测试 @DS(\"master\") 写操作 ---");
        if (shop != null) {
            shop.setArea(shop.getArea() + " 测试更新");
            shopService.update(shop);
            log.info("店铺更新完成，查看日志中引用的 DataSource 是否为 master");
        }
        
        log.info("============== 测试结束 ==============");
    }
}
