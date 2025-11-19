package tech.maxanderson.calculator.subtract.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tech.maxanderson.calculator.subtract.dto.SubtractResponse;
import tech.maxanderson.calculator.subtract.exception.ServiceException;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationService {

    private final WebClient addServiceClient;

    public Double subtract(Double num1, Double num2) {
        log.info("CalculationService.subtract: Performing subtraction {} - {}", num1, num2);

        // Negate num2 to convert subtraction to addition
        Double negativeNum2 = -num2;

        try {
            // Artificial delay to demonstrate caching benefits
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("CalculationService.subtract: Thread interrupted during calculation", e);
        }

        log.info("CalculationService.subtract: Calling add service with {} + {}", num1, negativeNum2);

        try {
            // Call the add service
            Map<String, Object> response = addServiceClient
                    .post()
                    .uri("/api/v1/add")
                    .bodyValue(Map.of("num1", num1, "num2", negativeNum2))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("result")) {
                log.error("CalculationService.subtract: Invalid response from add service");
                throw new ServiceException("Error from add service: Invalid response format");
            }

            Object resultObj = response.get("result");
            if (resultObj instanceof Number number) {
                Double result = number.doubleValue();
                log.info("CalculationService.subtract: Calculation completed successfully, result: {}", result);
                return result;
            }

            log.error("CalculationService.subtract: Unexpected result type from add service");
            throw new ServiceException("Error from add service: Unexpected result type");

        } catch (WebClientResponseException e) {
            log.error("CalculationService.subtract: Error calling add service: Status {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());

            String errorMessage = "Error from add service";
            try {
                Map<String, Object> errorBody = e.getResponseBodyAs(Map.class);
                if (errorBody != null) {
                    if (errorBody.containsKey("message")) {
                        errorMessage = "Error from add service: " + errorBody.get("message");
                    } else if (errorBody.containsKey("error")) {
                        errorMessage = "Error from add service: " + errorBody.get("error");
                    }
                }
            } catch (Exception parseError) {
                log.warn("CalculationService.subtract: Could not parse error response", parseError);
            }

            throw new ServiceException(errorMessage, e);
        } catch (ServiceException e) {
            // Re-throw ServiceException as-is
            throw e;
        } catch (Exception e) {
            log.error("CalculationService.subtract: Unexpected error calling add service", e);
            throw new ServiceException("Error from add service: Unexpected error occurred", e);
        }
    }
}
