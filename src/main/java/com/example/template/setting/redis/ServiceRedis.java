package com.example.template.setting.redis;

import com.example.template.setting.security.AhaUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ServiceRedis {
    private final RedisTemplate<String, Object> redisTemplateIdUser;
    private final RedisTemplate<String, Object> redisTemplateIdTokenRefresh;

    public ServiceRedis(RedisTemplate<String, Object> redisTemplateIdUser,
                        @Qualifier("redisDatabaseIdTokenRefresh") RedisTemplate<String, Object> redisTemplateIdTokenRefresh) {
        this.redisTemplateIdUser = redisTemplateIdUser;
        this.redisTemplateIdTokenRefresh = redisTemplateIdTokenRefresh;
    }


    private <T> boolean setData(RedisTemplate<String, Object> redisTemplate, String key, T data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch(Exception e){
            log.error(e.toString());
            return false;
        }
    }

    private <T> boolean setData(RedisTemplate<String, Object> redisTemplate, String key, T data, long timeout) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch(Exception e){
            log.error(e.toString());
            return false;
        }
    }

    private <T> T getData(RedisTemplate<String, Object> redisTemplate, String key, Class<T> typeClass) {
        String value = (String)redisTemplate.opsForValue().get(key);

        if(value == null){
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.registerModule(new JavaTimeModule()).readValue(value, typeClass);
        } catch(Exception e){
            log.error(e.toString());
            return null;
        }
    }

    private boolean deleteData(RedisTemplate<String, Object> redisTemplate, String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    private Long getExpire(RedisTemplate<String, Object> redisTemplate, String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    public boolean setAuthAccessIdUser(String id, AhaUserDetails ahaUserDetails) {
        return setData(redisTemplateIdUser, id, ahaUserDetails);
    }

    public boolean setAuthAccessIdUserTimeout(String id, AhaUserDetails ahaUserDetails, long timeout) {
        return setData(redisTemplateIdUser, id, ahaUserDetails, timeout);
    }

    public AhaUserDetails getAuthAccessIdUser(String id) {
        return getData(redisTemplateIdUser, id, AhaUserDetails.class);
    }

    public Long getAuthAccessExpire(String id) {
        return getExpire(redisTemplateIdUser, id);
    }


    public boolean setAuthRefreshIdToken(String id, String token) {
        return setData(redisTemplateIdTokenRefresh, id, token);
    }

    public boolean setAuthRefreshIdTokenTimeout(String id, String token, long timeout) {
        return setData(redisTemplateIdTokenRefresh, id, token, timeout);
    }

    public String getAuthRefreshIdToken(String id) {
        return getData(redisTemplateIdTokenRefresh, id, String.class);
    }

    public Long getAuthRefreshExpire(String id) {
        return getExpire(redisTemplateIdTokenRefresh, id);
    }
}
