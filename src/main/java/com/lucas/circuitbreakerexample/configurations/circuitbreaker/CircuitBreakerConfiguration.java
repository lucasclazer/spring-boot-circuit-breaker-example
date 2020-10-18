package com.lucas.circuitbreakerexample.configurations.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.*;
import org.springframework.cloud.client.circuitbreaker.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig(){
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(3)
                .waitDurationInOpenState(Duration.ofMillis(5000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(2)
                .recordExceptions(Exception.class, IOException.class, TimeoutException.class)
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory(CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerConfig circuitBreakerConfig) {
        ReactiveResilience4JCircuitBreakerFactory factory = new ReactiveResilience4JCircuitBreakerFactory();
        factory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
        factory.configureDefault(s -> new Resilience4JConfigBuilder(s)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(5)).build())
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
        return factory;
    }

    @Bean
    public ReactiveCircuitBreaker circuitBreaker(ReactiveResilience4JCircuitBreakerFactory factory){
        return factory.create("default");
    }
}
