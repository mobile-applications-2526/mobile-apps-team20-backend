package com.mbproyect.campusconnect.serviceimpl.auth;

import com.mbproyect.campusconnect.service.auth.TokenStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class TokenStorageServiceImpl implements TokenStorageService {

    private final StringRedisTemplate redisTemplate;

    public TokenStorageServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
    }

    @Override
    public void addToken(String key, String value, Duration ttl) {
        if (ttl == null) {
            redisTemplate.opsForValue().set(key, value);
            return;
        }
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public boolean isTokenValid(String key) {
        return redisTemplate.opsForValue().get(key) != null;
    }

    @Override
    public String getToken(String key) {
        String token = redisTemplate.opsForValue().get(key);
        return  token != null ?  token : "";
    }

    @Override
    public void removeToken(String key) {
        redisTemplate.delete(key);
    }
}
