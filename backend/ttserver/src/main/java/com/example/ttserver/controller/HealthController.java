package com.example.ttserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    public String health() {
        return "OK";
    }

    @GetMapping("/")
    @Operation(summary = "API Root", description = "Root endpoint for the API")
    public String root() {
        return "TomorrowTrader API is running";
    }
}
