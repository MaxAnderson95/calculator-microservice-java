package tech.maxanderson.calculator.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.maxanderson.calculator.frontend.dto.CalculateRequest;
import tech.maxanderson.calculator.frontend.dto.CalculationResult;
import tech.maxanderson.calculator.frontend.exception.CalculatorException;
import tech.maxanderson.calculator.frontend.service.CalculationService;
import tech.maxanderson.calculator.frontend.service.LoggingService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalculationService calculationService;

    @MockBean
    private LoggingService loggingService;

    @Test
    void testCalculate_Add_Success() throws Exception {
        CalculateRequest request = new CalculateRequest();
        request.setOperation("add");
        request.setNum1(10.0);
        request.setNum2(5.0);

        when(calculationService.calculate(anyString(), anyDouble(), anyDouble()))
                .thenReturn(new CalculationResult(15.0, false));

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("15.0"));

        verify(loggingService).logCalculation("add", 10.0, 5.0, 15.0, false);
    }

    @Test
    void testCalculate_InvalidOperation() throws Exception {
        CalculateRequest request = new CalculateRequest();
        request.setOperation("invalid");
        request.setNum1(10.0);
        request.setNum2(5.0);

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Invalid operation: invalid"));
    }

    @Test
    void testCalculate_ServiceError() throws Exception {
        CalculateRequest request = new CalculateRequest();
        request.setOperation("divide");
        request.setNum1(10.0);
        request.setNum2(0.0);

        when(calculationService.calculate(anyString(), anyDouble(), anyDouble()))
                .thenThrow(new CalculatorException("Cannot divide by zero"));

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Cannot divide by zero"));
    }

    @Test
    void testCalculate_MissingOperation() throws Exception {
        CalculateRequest request = new CalculateRequest();
        request.setNum1(10.0);
        request.setNum2(5.0);

        mockMvc.perform(post("/api/v1/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
