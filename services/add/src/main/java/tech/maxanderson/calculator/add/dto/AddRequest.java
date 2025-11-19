package tech.maxanderson.calculator.add.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRequest {
    @NotNull(message = "num1 is required")
    private Double num1;

    @NotNull(message = "num2 is required")
    private Double num2;
}
