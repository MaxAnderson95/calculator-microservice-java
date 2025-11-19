package tech.maxanderson.calculator.add.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tech.maxanderson.calculator.add.dto.AddRequest;
import tech.maxanderson.calculator.add.service.CalculationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddController.class)
class AddControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalculationService calculationService;

    @Test
    void testAdd_Success() throws Exception {
        AddRequest request = new AddRequest();
        request.setNum1(10.0);
        request.setNum2(5.0);

        when(calculationService.add(any(Double.class), any(Double.class)))
                .thenReturn(15.0);

        mockMvc.perform(post("/api/v1/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(15.0));
    }

    @Test
    void testAdd_MissingNum1() throws Exception {
        AddRequest request = new AddRequest();
        request.setNum2(5.0);

        mockMvc.perform(post("/api/v1/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAdd_MissingNum2() throws Exception {
        AddRequest request = new AddRequest();
        request.setNum1(10.0);

        mockMvc.perform(post("/api/v1/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
