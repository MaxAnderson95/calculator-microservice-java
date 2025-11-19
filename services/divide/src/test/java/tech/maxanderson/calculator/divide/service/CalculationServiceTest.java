package tech.maxanderson.calculator.divide.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.maxanderson.calculator.divide.exception.DivisionByZeroException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @InjectMocks
    private CalculationService calculationService;

    @Test
    void testDivide_PositiveNumbers() {
        Double result = calculationService.divide(10.0, 5.0);
        assertEquals(2.0, result);
    }

    @Test
    void testDivide_NegativeNumbers() {
        Double result = calculationService.divide(-10.0, -5.0);
        assertEquals(2.0, result);
    }

    @Test
    void testDivide_MixedNumbers() {
        Double result = calculationService.divide(10.0, -5.0);
        assertEquals(-2.0, result);
    }

    @Test
    void testDivide_Decimals() {
        Double result = calculationService.divide(7.5, 2.5);
        assertEquals(3.0, result, 0.0001);
    }

    @Test
    void testDivide_ByZero_ThrowsException() {
        assertThrows(DivisionByZeroException.class, () -> {
            calculationService.divide(10.0, 0.0);
        });
    }

    @Test
    void testDivide_ZeroByNumber() {
        Double result = calculationService.divide(0.0, 5.0);
        assertEquals(0.0, result);
    }
}
