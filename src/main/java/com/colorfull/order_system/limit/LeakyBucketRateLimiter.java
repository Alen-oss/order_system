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

    /**
     * 漏桶的最大容量
     */
    private final int capacity;

    /**
     * 漏桶每秒流出速率
     */
    private final int permitsPerSecond;

    /**
     * 漏出时间间隔，单位毫秒
     */
    private final long stableInterval;

    /**
     * 漏桶当前的水量
     */
    private long leftWater;

    /**
     * 上次刷新时间戳
     */
    private long lastTimeStamp;

    /**
     * 有参构造函数
     */
    public LeakyBucketRateLimiter(int permitsPerSecond, int capacity) {

        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
        this.lastTimeStamp = System.currentTimeMillis();
        // 这里的间隔时间大概率不是整数，是向下取整的
        this.stableInterval = 1000 / permitsPerSecond;
    }

    /**
     * 方法目的：检查是否有落入漏桶的条件
     */
    public synchronized boolean tryAcquire() {

        long nowTimeStamp = System.currentTimeMillis();
        long difference = nowTimeStamp - lastTimeStamp;
        // 这里必须是大于，不能包含等于，因为stableInterval本身是向下取整的
        if (difference > stableInterval) {
            this.lastTimeStamp = nowTimeStamp;
            leftWater = Math.max(0, leftWater - (difference * permitsPerSecond) / 1000);
        }
        // 如果未满，则放行；否则限流
        if (leftWater < capacity) {
            leftWater += 1;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {

        // 想要以恒定速率漏出流量，通常还需要配合一个FIFO队列来实现，当流量没有被抛弃时请求入队，然后再以恒定速率出队进行处理
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        ExecutorService singleThread = Executors.newSingleThreadExecutor();

        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(30, 20);
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

        // 模拟处理请求 固定速率漏水，这个速率即为permitsPerSecond，是间隔stableInterval执行一次任务
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!queue.isEmpty()) {
                System.out.println(queue.poll() + "被处理");
            }
        }, 0, rateLimiter.stableInterval, TimeUnit.MILLISECONDS);

        // 保证主线程不会退出
        while (true) {
            Thread.sleep(10000);
        }
    }
}
