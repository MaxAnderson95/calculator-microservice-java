package tech.maxanderson.calculator.frontend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class WebClientConfig {

    @Value("${service.add.url}")
    private String addServiceUrl;

    @Value("${service.subtract.url}")
    private String subtractServiceUrl;

    @Value("${service.multiply.url}")
    private String multiplyServiceUrl;

    @Value("${service.divide.url}")
    private String divideServiceUrl;

    @Bean
    public Map<String, WebClient> calculatorClients() {
        return Map.of(
                "add", WebClient.builder().baseUrl(addServiceUrl).build(),
                "subtract", WebClient.builder().baseUrl(subtractServiceUrl).build(),
                "multiply", WebClient.builder().baseUrl(multiplyServiceUrl).build(),
                "divide", WebClient.builder().baseUrl(divideServiceUrl).build()
        );
    }
}
