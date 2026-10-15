package com.example.demo.com.config;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * El generador aleatorio se declara como @Bean (igual que RestTemplate) para poder
 * inyectarlo por constructor y sustituirlo por un mock en los tests.
 */
@Configuration
public class RandomConfig {

    @Bean
    public RandomGenerator randomGenerator() {
        return new SecureRandom();
    }
}
