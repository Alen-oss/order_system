package com.colorfull.order_system.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * ZooKeeper实现分布式锁
 * Curator主要实现了以下四种锁：
 * 1）InterProcessMutex：分布式可重入排他锁（公平锁，创建临时顺序节点）
 * 2）InterProcessSemaphoreMutex：分布式排他锁（不可重入，公平锁，创建临时顺序节点）
 * 3）InterProcessReadWriteLock：分布式读写锁
 * 4）InterProcessMultiLock：将多个锁作为单个实体管理的容器
 */
public class ZkLock {

    public static void main(String[] args) throws Exception {
        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        client.start();
        // this mutex is fair，公平锁，内部采用临时序列化节点来抢占锁，序号最小的获取锁，添加watch事件监听上一个节点
        InterProcessMutex mutex = new InterProcessMutex(client, "/curator/mutex");
        try {
            mutex.acquire();
            System.out.println("执行业务...");
        } finally {
            mutex.release();
            client.close();
        }
        InterProcessReadWriteLock lock = new InterProcessReadWriteLock(client, "/lock");
        // maxLeases = 1，表示排他锁
        InterProcessMutex readMutex = lock.readLock();
        // maxLeases = Integer.MAX_VALUE，表示共享锁
        InterProcessMutex writeMutex = lock.writeLock();
        // 信号量默认为1，Semaphore中的计数器是递增的，release后信号量 + 1，acquire后信号量 - 1，当然一次性可以acquire多个信号量
        InterProcessSemaphoreMutex lock1 = new InterProcessSemaphoreMutex(client, "/lock1");

    }
}
