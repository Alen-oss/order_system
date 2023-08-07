package com.colorfull.order_system.limit;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch功能测试
 * 常见使用场景：主线程阻塞等待线程池任务完成
 */
public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(20);
        for (int i = 0; i < 20; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("执行任务--");
                    // countDown()方法并没有规定一个线程只能调用一次，当同一个线程调用多次countDown()方法时，每次都会使计数器减一
                    countDownLatch.countDown();
                    System.out.println("完成任务--");
                }
            });
        }
        System.out.println("主线程开始阻塞等待--");
        countDownLatch.await();
        System.out.println("主线程阻塞结束--");
        executor.shutdown();
    }
}
