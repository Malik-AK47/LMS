package com.malik.lms.auth.controller;

import com.malik.lms.auth.dto.request.AuthRequest;
import com.malik.lms.auth.dto.response.AuthResponse;
import com.malik.lms.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
 TODO:
 1. saparate auth/registeration and user
 2. Password encoder bean
 3. UUID
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest authRequest) throws Exception {

        return userService.register(authRequest);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest authRequest) {
        return userService.login(authRequest);
    }
}

