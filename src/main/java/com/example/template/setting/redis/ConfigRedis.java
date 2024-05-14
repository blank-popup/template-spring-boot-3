package com.example.template.setting.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class ConfigRedis {
    private final RedisProperties redisProperties;

    @Value("${spring.data.redis.index.id-user}")
    private int indexRedisIdUser;
    @Value("${spring.data.redis.index.id-token-refresh}")
    private int indexRedisIdTokenRefresh;

    private LettuceConnectionFactory creaLettuceConnectionFactory(int index) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisProperties.getHost());
        redisConfiguration.setPort(redisProperties.getPort());
        redisConfiguration.setPassword(redisProperties.getPassword());
        redisConfiguration.setDatabase(index);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
        return lettuceConnectionFactory;
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactoryIdUser() {
        return creaLettuceConnectionFactory(indexRedisIdUser);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplateIdUser(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    @Bean
    @Qualifier("redisDatabaseIdTokenRefresh")
    public RedisConnectionFactory redisConnectionFactoryIdTokenRefresh() {
        return creaLettuceConnectionFactory(indexRedisIdTokenRefresh);
    }

    @Bean
    @Qualifier("redisDatabaseIdTokenRefresh")
    public RedisTemplate<String, Object> redisTemplateIdTokenRefresh(@Qualifier("redisDatabaseIdTokenRefresh") RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }
}
