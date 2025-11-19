package tech.maxanderson.calculator.frontend.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@ConditionalOnProperty(name = "cache.type", havingValue = "memory", matchIfMissing = true)
public class InMemoryCacheService implements CacheService {

    private final Map<String, Double> cache = new ConcurrentHashMap<>();

    @Override
    public Double get(String operation, Double num1, Double num2) {
        String key = buildKey(operation, num1, num2);
        Double value = cache.get(key);

        if (value == null) {
            log.info("InMemoryCacheService.get: Cache miss for key: {}", key);
            return null;
        } else {
            log.info("InMemoryCacheService.get: Cache hit for key: {}, value: {}", key, value);
            return value;
        }
    }

    @Override
    public void set(String operation, Double num1, Double num2, Double value) {
        String key = buildKey(operation, num1, num2);
        cache.put(key, value);
        log.info("InMemoryCacheService.set: Successfully cached result for key: {}, value: {}", key, value);
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
