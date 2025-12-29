package io.github.angelo.TicketingSystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Ticketing System API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("authentication", "/api/auth");
        endpoints.put("users", "/api/users");
        endpoints.put("tickets", "/api/tickets");
        endpoints.put("categories", "/api/categories");
        endpoints.put("priorities", "/api/priorities");
        endpoints.put("statuses", "/api/statuses");
        endpoints.put("h2-console", "/h2-console");
        
        response.put("endpoints", endpoints);
        
        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("login", "POST /api/auth/login");
        authInfo.put("register", "POST /api/auth/register");
        authInfo.put("note", "Use 'Authorization: Bearer {token}' header for protected endpoints");
        
        response.put("authentication", authInfo);
        
        return response;
    }
}
