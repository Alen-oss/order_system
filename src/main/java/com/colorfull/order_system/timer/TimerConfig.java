package com.colorfull.order_system.timer;

import io.netty.util.HashedWheelTimer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Netty中客户端连接超时，通信双方心跳检测等场景都是基于这个时间轮组件完成的
 * 时间轮配置类
 * 第一次调用newTimeout添加定时任务时会触发worker线程启动
 * 添加的定时任务会先进入Mpsc Queue中，这个队列是一个多生产者单消费者的线程安全队列，保证了多线程并发添加定时任务的线程安全
 * worker线程的工作逻辑（do - while循环）：
 * 1）执行waitForNextTick方法计算出时针到下一次tick的时间间隔，然后sleep到下一次tick
 * 2）位运算获取当前tick在时间轮数组中的下标
 * 3）移除被取消的任务
 * 4）从MPSC Queue中取出任务添加对应的时间轮数组中
 * 5）执行当前时间轮中的到期任务
 * 对要求时效性非常严格的定时任务来说不友好，因为worker是单线程的，和其他基于本地的组件一样，占用程序内存太大
 */
@Configuration
public class TimerConfig {

    @Bean
    public HashedWheelTimer createTimer() {
        return new HashedWheelTimer();
    }
}
