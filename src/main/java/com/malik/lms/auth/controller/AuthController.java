package com.malik.lms.auth.controller;

import com.malik.lms.auth.dto.request.AuthRequest;
import com.malik.lms.auth.dto.response.AuthResponse;
import com.malik.lms.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService userService) {
        this.authService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest authRequest) throws Exception {

        return authService.register(authRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @GetMapping("/verify")
    public AuthResponse verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }
}


