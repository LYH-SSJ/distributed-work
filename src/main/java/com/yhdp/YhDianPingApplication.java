package com.yhdp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.yhdp.mapper")
@SpringBootApplication
public class YhDianPingApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhDianPingApplication.class, args);
    }

}
