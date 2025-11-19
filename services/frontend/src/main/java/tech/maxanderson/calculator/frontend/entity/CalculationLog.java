package tech.maxanderson.calculator.frontend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private Double num1;

    @Column(nullable = false)
    private Double num2;

    @Column(nullable = false)
    private Double result;

    @Column(nullable = false)
    private Boolean cacheHit;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CalculationLog(String operation, Double num1, Double num2, Double result, Boolean cacheHit) {
        this.operation = operation;
        this.num1 = num1;
        this.num2 = num2;
        this.result = result;
        this.cacheHit = cacheHit;
    }
}
