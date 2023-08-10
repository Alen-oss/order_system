package com.colorfull.order_system.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * redis setNX + lua脚本实现分布式锁
 */
@Component
public class RedisLock {

    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();


    /**
     * value随机生成策略：UUID.randomUUID().toString().replace("-", "")
     */
    public boolean tryAcquire(String key, String value, long timeout) throws NullPointerException {

        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 释放锁
     */
    public boolean tryRelease(String key, String value) {

        String redisValue = redisTemplate.opsForValue().get(key).toString();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT, Long.class);
        if (redisValue != null && redisValue.equals(value)) {
            // 参数二：key列表，参数三：arg（可多个）
            Long res = redisTemplate.execute(redisScript, Collections.singletonList(key), redisValue);
            return res == 1;
        }
        return false;
    }
}
