package tech.maxanderson.calculator.multiply.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.maxanderson.calculator.multiply.dto.MultiplyRequest;
import tech.maxanderson.calculator.multiply.dto.MultiplyResponse;
import tech.maxanderson.calculator.multiply.service.CalculationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class MultiplyController {

    private final CalculationService calculationService;

    @PostMapping("/multiply")
    public ResponseEntity<MultiplyResponse> multiply(@Valid @RequestBody MultiplyRequest request) {
        log.info("MultiplyController.multiply: Processing request for {} * {}", request.getNum1(), request.getNum2());

        Double result = calculationService.multiply(request.getNum1(), request.getNum2());

        log.info("MultiplyController.multiply: Successfully calculated result: {}", result);
        return ResponseEntity.ok(new MultiplyResponse(result));
    }
}
