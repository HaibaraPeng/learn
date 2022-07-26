package com.roc.springall.demo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Demo67Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo67Application.class, args);
    }

}
