package tech.maxanderson.calculator.divide.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.maxanderson.calculator.divide.exception.DivisionByZeroException;

@Service
@Slf4j
public class CalculationService {

    public Double divide(Double num1, Double num2) {
        log.info("CalculationService.divide: Performing division {} / {}", num1, num2);

        if (num2 == 0) {
            log.error("CalculationService.divide: Division by zero attempted with num1={}", num1);
            throw new DivisionByZeroException(String.format("Cannot divide %.2f by zero", num1));
        }

        try {
            // Artificial delay to demonstrate caching benefits
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CalculationService.divide: Thread interrupted during calculation", e);
        }

        Double result = num1 / num2;
        log.info("CalculationService.divide: Calculation completed successfully, result: {}", result);
        return result;
    }
}
