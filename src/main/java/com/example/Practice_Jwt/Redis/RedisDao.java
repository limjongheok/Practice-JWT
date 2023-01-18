package com.example.Practice_Jwt.Redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class RedisDao {
    private  final RedisTemplate<String, String> redisTemplate;



    public void setValues(String key, String data, Date refreshTokenExpireseIn){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data);
    }

    public void setValues(String key, String data, Duration duration){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data, duration);
    }

    public String getValues(String key){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(key);
        return refreshToken;
    }
    public void deleteValues(String key){
        redisTemplate.delete(key);
    }
}
