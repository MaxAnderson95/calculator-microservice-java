package tech.maxanderson.calculator.frontend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.maxanderson.calculator.frontend.entity.CalculationLog;
import tech.maxanderson.calculator.frontend.repository.CalculationLogRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoggingService {

    private final CalculationLogRepository calculationLogRepository;

    public void logCalculation(String operation, Double num1, Double num2, Double result, Boolean cacheHit) {
        log.info("LoggingService.logCalculation: Saving calculation log for operation {} with values {} and {}, result: {}, cache hit: {}",
                operation, num1, num2, result, cacheHit);

        CalculationLog logEntry = new CalculationLog(operation, num1, num2, result, cacheHit);
        calculationLogRepository.save(logEntry);

        log.info("LoggingService.logCalculation: Successfully saved calculation log for operation {}", operation);
    }
}
