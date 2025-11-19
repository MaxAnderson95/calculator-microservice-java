package tech.maxanderson.calculator.subtract.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.maxanderson.calculator.subtract.dto.SubtractRequest;
import tech.maxanderson.calculator.subtract.dto.SubtractResponse;
import tech.maxanderson.calculator.subtract.service.CalculationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class SubtractController {

    private final CalculationService calculationService;

    @PostMapping("/subtract")
    public ResponseEntity<SubtractResponse> subtract(@Valid @RequestBody SubtractRequest request) {
        log.info("SubtractController.subtract: Processing request for {} - {}", request.getNum1(), request.getNum2());

        Double result = calculationService.subtract(request.getNum1(), request.getNum2());

        log.info("SubtractController.subtract: Successfully calculated result: {}", result);
        return ResponseEntity.ok(new SubtractResponse(result));
    }
}
