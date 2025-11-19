package tech.maxanderson.calculator.multiply.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MultiplyRequest {
    @NotNull(message = "num1 is required")
    private Double num1;

    @NotNull(message = "num2 is required")
    private Double num2;
}
