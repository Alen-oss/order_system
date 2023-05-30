package com.colorfull.order_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// exclude是为了取消datasource的自动配置，不然项目启动时会因为没有配置datasource而启动不起来
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
// 开启对定时任务的支持
@EnableScheduling
public class OrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderSystemApplication.class, args);
    }

}
