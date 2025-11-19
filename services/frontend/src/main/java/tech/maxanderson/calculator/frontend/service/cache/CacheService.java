package tech.maxanderson.calculator.frontend.service.cache;

public interface CacheService {
    Double get(String operation, Double num1, Double num2);
    void set(String operation, Double num1, Double num2, Double value);
}
