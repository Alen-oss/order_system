package com.colorfull.order_system.task;

import java.util.concurrent.DelayQueue;

/**
 * 测试DelayQueue的实际效果
 */
public class DelayQueueTest {

    public static void main(String[] args) {

        // DelayQueue是无边界阻塞队列，加入其中的元素必须实现Delayed接口
        DelayQueue delayQueue = new DelayQueue();
        delayQueue.put(new MyDelayedTask("task1", 900));
        delayQueue.put(new MyDelayedTask("task2", 800));
        delayQueue.put(new MyDelayedTask("task3", 700));
        delayQueue.put(new MyDelayedTask("task4", 1000));
        delayQueue.put(new MyDelayedTask("task5", 11000));
        delayQueue.put(new MyDelayedTask("task6", 500));
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (delayQueue.isEmpty()) {
                        break;
                    }
                    MyDelayedTask myDelayedTask = null;
                    try {
                        myDelayedTask = (MyDelayedTask) delayQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(myDelayedTask);
                }
            }
        });
        t1.start();
    }
}
