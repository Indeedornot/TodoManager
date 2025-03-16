package com.bmisiek.todomanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bmisiek.todomanager.controller")
public class TodoManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoManagerApplication.class, args);
    }

}
