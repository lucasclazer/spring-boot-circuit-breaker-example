package com.lucas.circuitbreakerexample.controllers;

import com.lucas.circuitbreakerexample.services.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@RestController
public class ExampleController {
    private final ExampleService exampleService;
    private final ReactiveCircuitBreaker circuitBreaker;

    @Autowired
    public ExampleController(ExampleService exampleService, ReactiveCircuitBreaker circuitBreaker) {
        this.exampleService = exampleService;
        this.circuitBreaker = circuitBreaker;
    }

    @GetMapping(path = "/test")
    public Mono<String> test(@RequestParam Optional<String> name) throws Exception {
        var seconds = (long) (Math.random() * 10);
        return this.circuitBreaker.run(exampleService.testExample(name).delayElement(Duration.ofSeconds(seconds)), throwable -> testFallback(throwable));
    }

    public Mono<String> testFallback(Throwable throwable){
        return Mono.just("Fallback, microservice exceeded limit of requisitions");
    }
}
