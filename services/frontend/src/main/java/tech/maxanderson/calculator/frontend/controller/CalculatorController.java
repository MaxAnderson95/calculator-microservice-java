package tech.maxanderson.calculator.frontend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.maxanderson.calculator.frontend.dto.CalculateRequest;
import tech.maxanderson.calculator.frontend.dto.CalculationResult;
import tech.maxanderson.calculator.frontend.exception.CalculatorException;
import tech.maxanderson.calculator.frontend.service.CalculationService;
import tech.maxanderson.calculator.frontend.service.LoggingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class CalculatorController {

    private static final List<String> ALLOWED_OPERATIONS = List.of("add", "subtract", "multiply", "divide");

    private final CalculationService calculationService;
    private final LoggingService loggingService;

    @PostMapping("/calculate")
    public ResponseEntity<Double> calculate(@Valid @RequestBody CalculateRequest request) {
        log.info("CalculatorController.calculate: Processing request for operation {} with values {} and {}",
                request.getOperation(), request.getNum1(), request.getNum2());

        if (!ALLOWED_OPERATIONS.contains(request.getOperation())) {
            log.error("CalculatorController.calculate: Invalid operation requested: {}", request.getOperation());
            throw new CalculatorException("Invalid operation: " + request.getOperation());
        }

        CalculationResult result = calculationService.calculate(
                request.getOperation(),
                request.getNum1(),
                request.getNum2()
        );

        loggingService.logCalculation(
                request.getOperation(),
                request.getNum1(),
                request.getNum2(),
                result.getResult(),
                result.getCacheHit()
        );

        log.info("CalculatorController.calculate: Successfully completed operation {}, result: {}",
                request.getOperation(), result.getResult());
        return ResponseEntity.ok(result.getResult());
    }

}
