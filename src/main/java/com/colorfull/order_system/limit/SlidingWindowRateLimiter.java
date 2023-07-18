package com.colorfull.order_system.limit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SlidingWindowRateLimiter {

    /**
     * 每分钟限制请求数
     */
    private final long permitsPerMinute;

    /**
     * 单位时间划分的小周期（单位时间为1分钟，10s一个小格子窗口，一共6个格子）
     */
    private int CUB_CYCLE = 10;

    /**
     * 计数器, k为当前窗口的开始时间值秒，value为当前窗口的计数
     */
    private final TreeMap<Long, Integer> counters;

    public SlidingWindowRateLimiter(long permitsPerMinute) {
        this.permitsPerMinute = permitsPerMinute;
        this.counters = new TreeMap<>();
    }

    public synchronized boolean tryAcquire() {
        // 获取当前时间的所在的子窗口值；10s一个窗口
        long currentWindowTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / 10 * 10;
        System.out.println(currentWindowTime);
        // 获取当前窗口的请求总量
        int currentWindowCount = getCurrentWindowCount(currentWindowTime);
        System.out.println(currentWindowCount);
        if (currentWindowCount >= permitsPerMinute) {
            return false;
        }
        // 计数器 + 1
        counters.merge(currentWindowTime, 1, Integer::sum);
        return true;
    }

    /**
     * 获取当前窗口中的所有请求数（并删除所有无效的子窗口计数器）
     *
     * @param currentWindowTime 当前子窗口时间
     * @return 当前窗口中的计数
     */
    private int getCurrentWindowCount(long currentWindowTime) {
        // 计算出窗口的开始位置时间
        long startTime = currentWindowTime - 50;
        int result = 0;

        // 遍历当前存储的计数器，删除无效的子窗口计数器，并累加当前窗口中的所有计数器之和
        Iterator<Map.Entry<Long, Integer>> iterator = counters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            if (entry.getKey() < startTime) {
                iterator.remove();
            } else {
                result += entry.getValue();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(10);
        limiter.tryAcquire();
    }
}
