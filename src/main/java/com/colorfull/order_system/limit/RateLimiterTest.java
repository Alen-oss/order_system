package com.colorfull.order_system.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * guava限流组件RateLimiter使用
 * 限速范围实际上是不固定的：rate ~ rate + bucket数量
 */
public class RateLimiterTest {

    public static void main(String[] args) throws InterruptedException {

        String start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("start time:" + start);
        RateLimiter limiter = RateLimiter.create(5); // 这里的1表示每秒允许处理的量为1个
        limiter.acquire();
        Thread.sleep(5000);
        for (int i = 1; i <= 12; i++) {
            limiter.acquire();// 请求RateLimiter, 超过permits会被阻塞
            System.out.println("call execute.." + i);
            System.out.println(System.currentTimeMillis());
        }
        String end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println("end time:" + end);
    }
}
