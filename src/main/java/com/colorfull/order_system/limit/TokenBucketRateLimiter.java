package com.colorfull.order_system.limit;

/**
 * 简单的令牌桶实现
 * 常见思路上还会添加一个StopWatch类，作用为如果请求没有立刻拿到令牌让其阻塞等待
 * 所以令牌桶适合后台任务类限速，即阻塞等待型任务
 */
public class TokenBucketRateLimiter {

    /**
     * 令牌桶的容量「限流器允许的最大突发流量」
     */
    private final long capacity;
    /**
     * 令牌发放速率
     */
    private final long generatedPerSeconds;
    /**
     * 最后一个令牌发放的时间
     */
    long lastTokenTime = System.currentTimeMillis();
    /**
     * 当前令牌数量
     */
    private long currentTokens;

    /**
     * 添加令牌时间间隔：1/qps
     */
    double stableIntervalMicros;

    public TokenBucketRateLimiter(long generatedPerSeconds, int capacity) {
        this.generatedPerSeconds = generatedPerSeconds;
        this.capacity = capacity;
    }

    /**
     * 尝试获取令牌
     *
     * @return true表示获取到令牌，放行；否则为限流
     */
    public synchronized boolean tryAcquire() {
        /**
         * 计算令牌当前数量
         * 请求时间在最后令牌是产生时间相差大于等于额1s（为啥时1s？因为生成令牌的最小时间单位时s），则
         * 1. 重新计算令牌桶中的令牌数
         * 2. 将最后一个令牌发放时间重置为当前时间
         */
        long now = System.currentTimeMillis();
        // 这里的1000算法就是：1/qps，如果需要更精确的话可以使用微秒，1毫秒 = 1000微秒，即生成一个令牌的时间
        // 即类成员中是可以加上一个变量：生成一个令牌的时间间隔，大于等于这个间隔才去更新令牌数 + 刷新时间
        if (now - lastTokenTime >= 1000) {
            long newPermits = (now - lastTokenTime) / 1000 * generatedPerSeconds;
            currentTokens = Math.min(currentTokens + newPermits, capacity);
            lastTokenTime = now;
        }
        if (currentTokens > 0) {
            currentTokens--;
            return true;
        }
        return false;
    }

}
