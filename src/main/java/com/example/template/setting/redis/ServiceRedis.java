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
    private RedisTemplate<String, Object> redisTemplateTokenUser;
    private RedisTemplate<String, Object> redisTemplateIdToken;

    public ServiceRedis(RedisTemplate<String, Object> redisTemplateTokenUser,
                        @Qualifier("01") RedisTemplate<String, Object> redisTemplateIdToken) {
        this.redisTemplateTokenUser = redisTemplateTokenUser;
        this.redisTemplateIdToken = redisTemplateIdToken;
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
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
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
        return redisTemplate.delete(key);
    }

    private Long getExpire(RedisTemplate<String, Object> redisTemplate, String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }


    public boolean setAuthTokenUser(String token, AhaUserDetails userDetailsCustom) {
        return setData(redisTemplateTokenUser, token, userDetailsCustom);
    }

    public boolean setAuthTokenUserTimeout(String token, AhaUserDetails userDetailsCustom, long timeout) {
        return setData(redisTemplateTokenUser, token, userDetailsCustom, timeout);
    }

    public AhaUserDetails getAuthTokenUser(String token) {
        return getData(redisTemplateTokenUser, token, AhaUserDetails.class);
    }


    public boolean setAuthIdToken(String id, String token) {
        return setData(redisTemplateIdToken, id, token);
    }

    public boolean setAuthIdTokenTimeout(String id, String token, long timeout) {
        return setData(redisTemplateIdToken, id, token, timeout);
    }

    public String getAuthIdToken(String id) {
        return getData(redisTemplateIdToken, id, String.class);
    }


    // public void setAuthRefreshTokenId(String refreshToken, String id) {
    //     setData(redisTemplateRefreshTokenId, refreshToken, id);
    // }

    // public void setAuthRefreshTokenIdTimeout(String refreshToken, String id, long timeout) {
    //     setData(redisTemplateRefreshTokenId, refreshToken, id, timeout);
    // }

    // public Optional<String> getAuthRefreshTokenId(String refreshToken) {
    //     return getData(redisTemplateRefreshTokenId, refreshToken, String.class);
    // }

    // public void setValue(String key, String value) {
    //     ValueOperations<String, Object> values = redisTemplate.opsForValue();
    //     values.set(key, value);
    // }

    // public void setValue(String key, String value, Duration duration) {
    //     ValueOperations<String, Object> values = redisTemplate.opsForValue();
    //     values.set(key, value, duration);
    // }

    // @Transactional(readOnly=true)
    // public String getValue(String key) {
    //     ValueOperations<String, Object> values = redisTemplate.opsForValue();
    //     if (values.get(key) == null) {
    //         return "false";
    //     }
    //     return (String) values.get(key);
    // }

    // public void deleteValue(String key) {
    //     redisTemplate.delete(key);
    // }

    // public void expireValues(String key, int timeout) {
    //     redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    // }

    // public void setHashOps(String key, Map<String, String> value) {
    //     HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
    //     values.putAll(key, value);
    // }

    // @Transactional(readOnly=true)
    // public String getHashOps(String key, String hashKey) {
    //     HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
    //     return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    // }

    // public void deleteHashOps(String key, String hashKey) {
    //     HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
    //     values.delete(key, hashKey);
    // }

    // public boolean checkExistsValue(String value) {
    //     return !value.equals("false");
    // }
}
