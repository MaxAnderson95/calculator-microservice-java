package tech.maxanderson.calculator.add.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.maxanderson.calculator.add.dto.AddRequest;
import tech.maxanderson.calculator.add.dto.AddResponse;
import tech.maxanderson.calculator.add.service.CalculationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AddController {

    private final CalculationService calculationService;

    @PostMapping("/add")
    public ResponseEntity<AddResponse> add(@Valid @RequestBody AddRequest request) {
        log.info("AddController.add: Processing request for {} + {}", request.getNum1(), request.getNum2());

        Double result = calculationService.add(request.getNum1(), request.getNum2());

        log.info("AddController.add: Successfully calculated result: {}", result);
        return ResponseEntity.ok(new AddResponse(result));
    }
}
