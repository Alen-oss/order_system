package com.colorfull.order_system.lock.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkConfig {

    @Value("${zookeeper.address}")
    private String address;

    @Bean
    public CuratorFramework curatorFramework() {
        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        // 工厂方法创建Curator
        CuratorFramework client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();
        return client;
    }
}
