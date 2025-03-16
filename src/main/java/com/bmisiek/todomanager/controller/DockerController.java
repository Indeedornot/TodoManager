package com.bmisiek.todomanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DockerController {
    @GetMapping("/messages")
    public String getMessage() {
        return "Hello from Docker!";
    }
}