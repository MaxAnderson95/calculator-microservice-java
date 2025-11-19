package tech.maxanderson.calculator.multiply.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @InjectMocks
    private CalculationService calculationService;

    @Test
    void testMultiply_PositiveNumbers() {
        Double result = calculationService.multiply(10.0, 5.0);
        assertEquals(50.0, result);
    }

    @Test
    void testMultiply_NegativeNumbers() {
        Double result = calculationService.multiply(-10.0, -5.0);
        assertEquals(50.0, result);
    }

    @Test
    void testMultiply_MixedNumbers() {
        Double result = calculationService.multiply(10.0, -5.0);
        assertEquals(-50.0, result);
    }

    @Test
    void testMultiply_Zero() {
        Double result = calculationService.multiply(10.0, 0.0);
        assertEquals(0.0, result);
    }

    @Test
    void testMultiply_Decimals() {
        Double result = calculationService.multiply(2.5, 4.0);
        assertEquals(10.0, result, 0.0001);
    }
}
