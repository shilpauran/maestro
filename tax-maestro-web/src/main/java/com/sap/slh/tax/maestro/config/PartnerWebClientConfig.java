package com.sap.slh.tax.maestro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PartnerWebClientConfig {
    
    @Bean
    public WebClient partnerWebClient() {
        return WebClient.create();
    }

}
