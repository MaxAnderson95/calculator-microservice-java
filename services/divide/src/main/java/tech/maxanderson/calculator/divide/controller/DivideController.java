package tech.maxanderson.calculator.divide.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.maxanderson.calculator.divide.dto.DivideRequest;
import tech.maxanderson.calculator.divide.dto.DivideResponse;
import tech.maxanderson.calculator.divide.service.CalculationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DivideController {

    private final CalculationService calculationService;

    @PostMapping("/divide")
    public ResponseEntity<DivideResponse> divide(@Valid @RequestBody DivideRequest request) {
        log.info("DivideController.divide: Processing request for {} / {}", request.getNum1(), request.getNum2());

        Double result = calculationService.divide(request.getNum1(), request.getNum2());

        log.info("DivideController.divide: Successfully calculated result: {}", result);
        return ResponseEntity.ok(new DivideResponse(result));
    }

}
