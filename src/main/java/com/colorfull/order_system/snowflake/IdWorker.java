package com.colorfull.order_system.snowflake;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 这里补充一下计算机位运算知识点
 * 原码、反码、补码
 * 正数的反码和补码就是原码，负数的反码符号位不变，其余按位取反，负数的补码就是求反码然后+1
 * 对于计算机存储数值的形式来说，所有的数值存储都是以补码的形式存储的，同理，计算机内所有的运算操作也都是以补码的形式进行的
 * 对于运算结果而言，若结果为负数，则符号位不变，然后求反+1就是最终结果，若结果为正数则无需变化
 * 负数的左移：符号位不变，直接乘以2的位移数次方
 * 负数的右移：求补码，右移相应位数，高位补1，得到的结果再次求反+1，即得最终结果
 *
 * 雪花算法理论知识
 * 64位bit表示分布式ID，Java中64位bit是Long类型
 * 第一部分：占用1bit，是一个符号位，始终为0，代表正数
 * 第二部分：占用41位，是时间戳
 * 第三部分：占用10位，可表示机器数
 * 第四部分：占用12位，是自增序列
 */
public class IdWorker {

    // 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
    private final static long twepoch = 1288834974657L;
    // 机器标识位数，即占用位数
    private final static long workerIdBits = 5L;
    // 数据中心标识位数，即占用位数
    private final static long datacenterIdBits = 5L;
    // 毫秒内自增位，即占用位数
    private final static long sequenceBits = 12L;

    // 机器ID偏左移12位
    private final static long workIdShift = sequenceBits;
    // 数据中心ID偏左移17位
    private final static long datacenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒左移22位
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 机器ID最大值，^异或操作，相同为0，不同为1，最大值为31，即5bit：11111
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 数据中心ID最大值，同上，最大值为31
    private final static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    // 毫秒内自增位最大值为4095，即0000...1111 1111 1111
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 上次生产id时间戳
    private static long lastTimestamp = -1L;
    // 初始序列号
    private long sequence = 0L;
    // 机器标识id部分
    private final long workerId;
    // 数据中心标识id部分
    private final long datacenterId;

    public IdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getWorkerId(datacenterId, maxWorkerId);
    }

    public IdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 获取下一个ID
     * 同步操作
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        // 对时钟回拨的情况进行检验
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        // 同一个时间戳下生成的ID
        if (lastTimestamp == timestamp) {
            // 对sequence进行与操作，实际上是为了判断是否超过区间最大值4095
            sequence = (sequence +1) & sequenceMask;
            // sequence等于0的话说明此时序列号已经超过区间最大值，执行操作生成新的时间戳，将这个ID放入到新的时间戳序列中
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果和上次生成ID的时间戳不一致，则序列号重新归零
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // 左移后执行或操作合并bit
        long nextId = ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workIdShift) | sequence;
        return nextId;
    }

    /**
     * 生成新的时间戳（新的时间戳需要在lastTimestamp后）
     */
    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 生成当前时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取机器ID
     */
    protected static long getWorkerId(long datacenterId, long maxWorkerId) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            mpid.append(name.split("@")[0]);
        }
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取数据中心ID
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0l;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1])
                      | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            System.out.println("getDatacenterId: " + e.getMessage());
        }
        return id;
    }

    public static void main(String[] agrs) {
        IdWorker idWorker = new IdWorker(31, 31);
        System.out.println("idWorker = " + idWorker.nextId());
        IdWorker id = new IdWorker();
        System.out.println("id = " + id.nextId());
        System.out.println(id.datacenterId);
        System.out.println(id.workerId);
    }

}
