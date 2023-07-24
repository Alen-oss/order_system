package com.colorfull.order_system.timer;

import io.netty.util.HashedWheelTimer;
import java.util.concurrent.TimeUnit;

public class HashedWheelTimerTest {

    public static void main(String[] args) {

        HashedWheelTimer timer = new HashedWheelTimer();
        OrderTask orderTask1 = new OrderTask("1", 0);
        timer.newTimeout(orderTask1, 72, TimeUnit.HOURS);
        OrderTask orderTask2 = new OrderTask("2", 1);
        timer.newTimeout(orderTask2, 10, TimeUnit.SECONDS);
    }
}
