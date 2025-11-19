package tech.maxanderson.calculator.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CalculateRequest {
    @NotBlank(message = "operation is required")
    private String operation;

    @NotNull(message = "num1 is required")
    private Double num1;

    @NotNull(message = "num2 is required")
    private Double num2;
}
