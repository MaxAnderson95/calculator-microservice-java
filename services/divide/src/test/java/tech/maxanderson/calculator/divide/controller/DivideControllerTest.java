package tech.maxanderson.calculator.divide.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.maxanderson.calculator.divide.dto.DivideRequest;
import tech.maxanderson.calculator.divide.exception.DivisionByZeroException;
import tech.maxanderson.calculator.divide.service.CalculationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DivideController.class)
class DivideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalculationService calculationService;

    @Test
    void testDivide_Success() throws Exception {
        DivideRequest request = new DivideRequest();
        request.setNum1(10.0);
        request.setNum2(5.0);

        when(calculationService.divide(any(Double.class), any(Double.class)))
                .thenReturn(2.0);

        mockMvc.perform(post("/api/v1/divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(2.0));
    }

    @Test
    void testDivide_ByZero() throws Exception {
        DivideRequest request = new DivideRequest();
        request.setNum1(10.0);
        request.setNum2(0.0);

        when(calculationService.divide(any(Double.class), any(Double.class)))
                .thenThrow(new DivisionByZeroException("Cannot divide by zero"));

        mockMvc.perform(post("/api/v1/divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Cannot divide by zero"));
    }
}
