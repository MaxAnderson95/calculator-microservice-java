package tech.maxanderson.calculator.add.service;

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
    void testAdd_PositiveNumbers() {
        Double result = calculationService.add(10.0, 5.0);
        assertEquals(15.0, result);
    }

    @Test
    void testAdd_NegativeNumbers() {
        Double result = calculationService.add(-10.0, -5.0);
        assertEquals(-15.0, result);
    }

    @Test
    void testAdd_MixedNumbers() {
        Double result = calculationService.add(10.0, -5.0);
        assertEquals(5.0, result);
    }

    @Test
    void testAdd_Zero() {
        Double result = calculationService.add(0.0, 0.0);
        assertEquals(0.0, result);
    }

    @Test
    void testAdd_Decimals() {
        Double result = calculationService.add(1.5, 2.3);
        assertEquals(3.8, result, 0.0001);
    }
}
