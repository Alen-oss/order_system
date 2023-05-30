package com.colorfull.order_system.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MyTask {

    // Spring Task的原理是在初始化bean时借助ScheduledAnnotationBeanPostProcessor拦截@Scheduled注解所标识的方法来完成的，其底层的实现是依赖jdk并发包中的ScheduledThreadPoolExecutor。
    // cron表达式：second /minute /hour /day of month /month /day of week
    // 默认是单线程执行，同步阻塞执行，若要自定义线程池的话，可以结合使用@Async异步注解指定自定义的线程池bean
    @Scheduled(cron = "0 0 1 * * *")
    public void task() {
        System.out.println("my task has been stared");
    }
}
