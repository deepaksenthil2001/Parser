package com.smartcode.analyzer.controller;

import com.smartcode.analyzer.model.User;
import com.smartcode.analyzer.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    // Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String email = body.get("email");
            String password = body.get("password");
            User u = authService.register(name, email, password);
            // return minimal user info (no password)
            return ResponseEntity.ok(Map.of(
                    "id", u.getId(),
                    "name", u.getName(),
                    "email", u.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");
            User u = authService.login(email, password);
            // simple response (no token) — return user info
            return ResponseEntity.ok(Map.of(
                    "id", u.getId(),
                    "name", u.getName(),
                    "email", u.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
