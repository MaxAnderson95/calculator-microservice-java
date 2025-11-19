package tech.maxanderson.calculator.frontend.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "cache.type", havingValue = "redis")
public class RedisCacheService implements CacheService {

    private final RedisTemplate<String, Double> redisTemplate;

    @Override
    public Double get(String operation, Double num1, Double num2) {
        String key = buildKey(operation, num1, num2);

        try {
            Double value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                log.info("RedisCacheService.get: Cache miss for key: {}", key);
                return null;
            } else {
                log.info("RedisCacheService.get: Cache hit for key: {}, value: {}", key, value);
                return value;
            }
        } catch (Exception e) {
            log.warn("RedisCacheService.get: Cache miss due to Redis error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void set(String operation, Double num1, Double num2, Double value) {
        String key = buildKey(operation, num1, num2);

        try {
            redisTemplate.opsForValue().set(key, value);
            log.info("RedisCacheService.set: Successfully cached result for key: {}, value: {}", key, value);
        } catch (Exception e) {
            log.warn("RedisCacheService.set: Failed to set cache due to Redis error: {}", e.getMessage());
        }
    }

    private String buildKey(String operation, Double num1, Double num2) {
        // For commutative operations, sort the operands
        if ("add".equals(operation) || "multiply".equals(operation)) {
            Double[] sorted = {num1, num2};
            Arrays.sort(sorted);
            return String.format("%s:%s:%s", operation, sorted[0], sorted[1]);
        }
        return String.format("%s:%s:%s", operation, num1, num2);
    }
}
