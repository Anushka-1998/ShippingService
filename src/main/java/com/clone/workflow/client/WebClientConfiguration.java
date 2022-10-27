package com.clone.workflow.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient webClient(WebClient.Builder builder)
    {
        return builder.build();
    }
    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}
