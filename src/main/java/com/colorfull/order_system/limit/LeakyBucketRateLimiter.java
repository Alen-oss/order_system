package com.colorfull.order_system.limit;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 漏桶简单实现
 * 固定速率流出，sentinel限流的思路就是漏桶，在实战时最好选择（简单，稳定）
 */
public class LeakyBucketRateLimiter {

    // 桶的容量
    private final int capacity;
    // 漏出速率
    private final int permitsPerSecond;
    // 剩余水量
    private long leftWater;
    // 上次注入时间
    private long timeStamp = System.currentTimeMillis();

    public LeakyBucketRateLimiter(int permitsPerSecond, int capacity) {
        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
    }

    /**
     * 这里实际上只是对流量是否抛弃做了检验，并没有以很定速率漏出流量
     */
    public synchronized boolean tryAcquire() {
        //1. 计算剩余水量
        long now = System.currentTimeMillis();
        long timeGap = (now - timeStamp) / 1000;
        leftWater = Math.max(0, leftWater - timeGap * permitsPerSecond);
        timeStamp = now;

        // 如果未满，则放行；否则限流
        if (leftWater < capacity) {
            leftWater += 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {

        // 想要以很定速率漏出流量，通常还需要配合一个FIFO队列来实现，当流量没有被抛弃时请求入队，然后再以恒定速率出队进行处理
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorService singleThread = Executors.newSingleThreadExecutor();

        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(20, 20);
        // 存储流量的队列
        Queue<Integer> queue = new LinkedList<>();
        // 模拟请求  不确定速率注水
        singleThread.execute(() -> {
            int count = 0;
            while (true) {
                count++;
                boolean flag = rateLimiter.tryAcquire();
                if (flag) {
                    queue.offer(count);
                    System.out.println(count + "--------流量被放行--------");
                } else {
                    System.out.println(count + "流量被限制");
                }
                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 模拟处理请求 固定速率漏水
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!queue.isEmpty()) {
                System.out.println(queue.poll() + "被处理");
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        // 保证主线程不会退出
        while (true) {
            Thread.sleep(10000);
        }
    }
}
