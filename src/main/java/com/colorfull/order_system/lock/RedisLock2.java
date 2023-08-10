package com.colorfull.order_system.lock;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Redisson客户端实现分布式锁
 */
public class RedisLock2 {

    @Autowired
    private RedissonClient redissonClient;

    public static void main(String[] args) {

        RedisLock2 redisLock = new RedisLock2();
        // 普通锁，非公平锁
        RLock lock1 = redisLock.redissonClient.getLock("anyLock");
        try {
            lock1.lockInterruptibly();
        } catch (InterruptedException interruptedException) {
            System.out.println("获取lock1锁的线程被中断...");
        }

        try {
            if (lock1.tryLock()) {
                System.out.println("执行业务逻辑...");
            }
        } finally {
            // 一定要使用try..finally..结构，最后unlock释放锁
            lock1.unlock();
        }
        // 多锁
        RLock lock2 = redisLock.redissonClient.getMultiLock();
        // 红锁
        RLock lock3 = redisLock.redissonClient.getRedLock();
        // 公平锁
        RLock lock4 = redisLock.redissonClient.getFairLock("anyLock");
        // 读写锁
        RReadWriteLock lock5 = redisLock.redissonClient.getReadWriteLock("anyLock");
        // 读锁
        RLock lock6 = lock5.readLock();
        // 写锁
        RLock lock7 = lock5.writeLock();
    }
}
