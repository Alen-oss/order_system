package com.colorfull.order_system.timer;

import io.netty.util.HashedWheelTimer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 这个类本身存在的意义就是在容器启动时，将超时订单重新导入时间轮中
 * 对于实力重新上线这种情况的补偿处理
 */
@Component
public class TimerProvider implements InitializingBean {

    @Autowired
    private HashedWheelTimer hashedWheelTimer;

    // @Autowired
    // private OrderDao orderDao;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 调用Dao层方法将状态订单放入时间轮中
        // 同时判断这些订单有没有过期的，过期的直接更新状态，不用放入时间轮中（接单时间、投放时间）
        // 放入时间轮中订单超时时间重新计算（过期时间 - 当前时间）
    }
}
