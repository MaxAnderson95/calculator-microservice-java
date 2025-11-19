package tech.maxanderson.calculator.add.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculationService {

    public Double add(Double num1, Double num2) {
        log.info("CalculationService.add: Performing addition {} + {}", num1, num2);

        try {
            // Artificial delay to demonstrate caching benefits
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CalculationService.add: Thread interrupted during calculation", e);
        }

        Double result = num1 + num2;
        log.info("CalculationService.add: Calculation completed successfully, result: {}", result);
        return result;
    }
}
