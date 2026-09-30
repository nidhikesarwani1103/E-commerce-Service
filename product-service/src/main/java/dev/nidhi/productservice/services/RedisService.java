package dev.nidhi.productservice.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(String key, String hashkey, Object hashValue){
        redisTemplate.opsForHash().put(key, hashkey, hashValue);
    }

    public <T> T get(String key, String hashKey, Class<T> clazz) {

        Object value = redisTemplate.opsForHash().get(key, hashKey);

        if (value == null) {
            return null;
        }

        return objectMapper.convertValue(value, clazz);
    }
}
