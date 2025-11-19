package tech.maxanderson.calculator.subtract.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${add.service.url}")
    private String addServiceUrl;

    @Bean
    public WebClient addServiceClient() {
        return WebClient.builder()
                .baseUrl(addServiceUrl)
                .build();
    }
}
