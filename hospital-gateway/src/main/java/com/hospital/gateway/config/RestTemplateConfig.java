package com.hospital.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Bean de RestTemplate — usado por HospitalGatewayService
     * para consumir los microservicios via HTTP (principio SOA)
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
