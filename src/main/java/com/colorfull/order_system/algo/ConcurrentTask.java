package com.colorfull.order_system.algo;

import java.util.concurrent.*;

/**
 * 有1 - 100个任务，每10个任务是一个执行批次，批次内并发工作，批次外顺序执行，即先1 -10，11 - 20，...
 */
public class ConcurrentTask {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(10);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= 100; i++) {
            Task task = new Task(i, countDownLatch);
            executorService.submit(task);
            if (i % 10 == 0) {
                countDownLatch.await();;
                countDownLatch = new CountDownLatch(10);
            }
        }
        // 关闭线程池资源
        executorService.shutdown();
        // ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000));
    }

    /**
     * 任务类
     */
    static class Task implements Runnable {

        private int id;

        private CountDownLatch countDownLatch;

        public Task(int id, CountDownLatch countDownLatch) {
            this.id = id;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            System.out.println("执行任务：" + this.id);
            this.countDownLatch.countDown();
        }
    }
}
