package com.nhj.librarymanage.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public <T> void save(String key, T data) {
        redisTemplate.opsForValue().set(key, data);
    }

    public <T> void save(String key, T data, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, data, timeout, unit);
    }

    public <T> void save(String key, T data, Duration timeout) {
        redisTemplate.opsForValue().set(key, data, timeout);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return clazz.cast(value);
    }

    public String getAndDelete(String key) {
        // return redisTemplate.opsForValue().getAndDelete(key);
        return stringRedisTemplate.opsForValue().getAndDelete(key); // String을 돌려주냐.. Object를 돌려주냐..? ok
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public long deleteAll(Collection<String> keys) {
        Long deletedCount = redisTemplate.delete(keys);
        return deletedCount == null ? 0L : deletedCount;
    }

}
