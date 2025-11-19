package tech.maxanderson.calculator.multiply.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationService {

    public Double multiply(Double num1, Double num2) {
        log.info("CalculationService.multiply: Performing multiplication {} * {}", num1, num2);

        try {
            // Artificial delay to demonstrate caching benefits
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CalculationService.multiply: Thread interrupted during calculation", e);
        }

        Double result = num1 * num2;
        log.info("CalculationService.multiply: Calculation completed successfully, result: {}", result);
        return result;
    }
}
