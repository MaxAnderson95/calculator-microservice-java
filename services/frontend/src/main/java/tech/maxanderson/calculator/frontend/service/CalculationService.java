package tech.maxanderson.calculator.frontend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tech.maxanderson.calculator.frontend.dto.CalculationResult;
import tech.maxanderson.calculator.frontend.exception.CalculatorException;
import tech.maxanderson.calculator.frontend.exception.ServiceUnavailableException;
import tech.maxanderson.calculator.frontend.service.cache.CacheService;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationService {

    private final CacheService cacheService;
    private final Map<String, WebClient> calculatorClients;

    public CalculationResult calculate(String operation, Double num1, Double num2) {
        log.info("CalculationService.calculate: Starting calculation for operation {} with {} and {}",
                operation, num1, num2);

        // Check cache first
        Double cachedResult = cacheService.get(operation, num1, num2);
        if (cachedResult != null) {
            log.info("CalculationService.calculate: Cache hit for operation {}, returning cached result: {}",
                    operation, cachedResult);
            return new CalculationResult(cachedResult, true);
        }

        log.info("CalculationService.calculate: Cache miss for operation {}, calling {} service",
                operation, operation);

        // Get the appropriate client
        WebClient client = calculatorClients.get(operation);
        if (client == null) {
            log.error("CalculationService.calculate: Unknown operation: {}", operation);
            throw new CalculatorException("Unknown operation: " + operation);
        }

        // Call the service
        try {
            Map<String, Object> response = client
                    .post()
                    .uri("/api/v1/" + operation)
                    .bodyValue(Map.of("num1", num1, "num2", num2))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("result")) {
                log.error("CalculationService.calculate: Invalid response from {} service", operation);
                throw new CalculatorException("Invalid response from " + operation + " service");
            }

            Object resultObj = response.get("result");
            Double result;
            if (resultObj instanceof Number number) {
                result = number.doubleValue();
            } else {
                log.error("CalculationService.calculate: Unexpected result type from {} service", operation);
                throw new CalculatorException("Unexpected result type from " + operation + " service");
            }

            // Cache the result
            log.info("CalculationService.calculate: Caching result for operation {}", operation);
            cacheService.set(operation, num1, num2, result);

            log.info("CalculationService.calculate: Successfully completed operation {}, result: {}",
                    operation, result);
            return new CalculationResult(result, false);

        } catch (WebClientResponseException e) {
            log.error("CalculationService.calculate: Error calling {} service: Status {}, Body: {}",
                    operation, e.getStatusCode(), e.getResponseBodyAsString());
            String errorMessage = "Error from " + operation + " service";

            try {
                Map<String, Object> errorBody = e.getResponseBodyAs(Map.class);
                if (errorBody != null) {
                    if (errorBody.containsKey("message")) {
                        String message = errorBody.get("message").toString();
                        // Check if message already contains service attribution
                        if (message.startsWith("Error from")) {
                            errorMessage = message;
                        } else {
                            errorMessage = "Error from " + operation + " service: " + message;
                        }
                    } else if (errorBody.containsKey("detail")) {
                        errorMessage = "Error from " + operation + " service: " + errorBody.get("detail");
                    } else if (errorBody.containsKey("error")) {
                        errorMessage = "Error from " + operation + " service: " + errorBody.get("error");
                    }
                }
            } catch (Exception parseError) {
                log.warn("CalculationService.calculate: Could not parse error response", parseError);
            }

            throw new ServiceUnavailableException(errorMessage, e);
        } catch (Exception e) {
            log.error("CalculationService.calculate: Unexpected error calling {} service", operation, e);
            throw new ServiceUnavailableException("Error from " + operation + " service: Unexpected error occurred", e);
        }
    }
}
