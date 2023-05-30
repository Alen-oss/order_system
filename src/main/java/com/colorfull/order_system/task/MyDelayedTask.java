package com.colorfull.order_system.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Delayed的实现类，需要实现两个方法：getDelayed + compareTo
 */
public class MyDelayedTask implements Delayed {

    private String name;
    private long start = System.currentTimeMillis();
    private long time;

    public MyDelayedTask(String name, long time) {
        this.name = name;
        this.time = time;
    }

    /**
     * 获取到期时间
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert((start + time) - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 此对象和一个特殊的值进行比较，返回一个数值
     * 负数 = 此对象小于比较值，提前到期
     * 0 = 两个对象相等，一样的到期时间
     * 证书 = 此对象大于比较值，到期时间晚于比较值
     * @param o
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    @Override
    public String toString() {
        return "任务名称：" + this.name + ", 到期时间：" + this.time;
    }

}
