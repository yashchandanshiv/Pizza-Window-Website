package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    // @GetMapping("/")
    // public ResponseEntity<Map<String, String>> root() {
    //     Map<String, String> response = new HashMap<>();
    //     response.put("message", "Welcome to Pizza Window - Food Frenzy API");
    //     response.put("status", "Server is running");
    //     response.put("documentation", "API endpoints available at /api/*");
    //     return ResponseEntity.ok(response);
    // }

    @GetMapping("/api/home")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to Pizza Window - Food Frenzy API");
        response.put("status", "API is running successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/home/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Application is healthy");
        return ResponseEntity.ok(response);
    }
}
