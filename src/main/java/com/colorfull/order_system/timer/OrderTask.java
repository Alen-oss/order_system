package com.colorfull.order_system.timer;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;

public class OrderTask implements TimerTask {

    private String orderId;

    private Integer status;

    public OrderTask(String orderId, Integer status) {
        this.orderId = orderId;
        this.status = status;
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        switch (this.status) {
            case 0:
                System.out.println(String.format("订单：%s未在规定时间内接单，发生流单", this.orderId));
                break;
            case 1:
                System.out.println(String.format("订单：%s未在规定时间内上传链接，发生流单", this.orderId));
                break;
            default:
                System.out.println(String.format("订单：%s流程正常", this.orderId));
        }
    }
}
