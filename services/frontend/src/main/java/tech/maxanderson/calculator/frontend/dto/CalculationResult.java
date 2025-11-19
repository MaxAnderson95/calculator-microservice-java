package tech.maxanderson.calculator.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalculationResult {
    private Double result;
    private Boolean cacheHit;
}
