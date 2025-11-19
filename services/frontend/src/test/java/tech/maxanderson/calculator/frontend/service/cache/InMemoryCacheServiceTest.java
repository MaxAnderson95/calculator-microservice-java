package tech.maxanderson.calculator.frontend.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCacheServiceTest {

    private InMemoryCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new InMemoryCacheService();
    }

    @Test
    void testGet_CacheMiss() {
        Double result = cacheService.get("add", 1.0, 2.0);
        assertNull(result);
    }

    @Test
    void testSetAndGet_CacheHit() {
        cacheService.set("add", 1.0, 2.0, 3.0);
        Double result = cacheService.get("add", 1.0, 2.0);
        assertEquals(3.0, result);
    }

    @Test
    void testCommutativeOperation_Add() {
        cacheService.set("add", 1.0, 2.0, 3.0);
        // Should find cache with reversed operands
        Double result = cacheService.get("add", 2.0, 1.0);
        assertEquals(3.0, result);
    }

    @Test
    void testCommutativeOperation_Multiply() {
        cacheService.set("multiply", 3.0, 4.0, 12.0);
        // Should find cache with reversed operands
        Double result = cacheService.get("multiply", 4.0, 3.0);
        assertEquals(12.0, result);
    }

    @Test
    void testNonCommutativeOperation_Subtract() {
        cacheService.set("subtract", 5.0, 3.0, 2.0);
        // Should NOT find cache with reversed operands
        Double result = cacheService.get("subtract", 3.0, 5.0);
        assertNull(result);
    }

    @Test
    void testNonCommutativeOperation_Divide() {
        cacheService.set("divide", 10.0, 2.0, 5.0);
        // Should NOT find cache with reversed operands
        Double result = cacheService.get("divide", 2.0, 10.0);
        assertNull(result);
    }
}
