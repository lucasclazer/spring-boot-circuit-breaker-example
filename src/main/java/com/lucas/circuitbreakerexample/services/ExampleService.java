package com.lucas.circuitbreakerexample.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class ExampleService {

    public Mono<String> testExample(Optional<String> test) {
        return test.map( str -> Mono.just("Test " + str)).orElse(Mono.error(new NullPointerException()));
    }

    public Mono<String> fallBackTestExample(Throwable e){
        return Mono.just("FallBack");
    }

}
