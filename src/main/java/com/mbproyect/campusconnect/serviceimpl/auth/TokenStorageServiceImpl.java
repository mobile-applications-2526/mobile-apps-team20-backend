package com.mbproyect.campusconnect.serviceimpl;

import com.mbproyect.campusconnect.service.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final StringRedisTemplate redisTemplate;

    public StorageServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
    }

    @Override
    public void saveValue(String key, String value) {

    }

    @Override
    public boolean isKeySaved(String key) {
        return false;
    }

    @Override
    public String getValue(String key) {
        return "";
    }
}
