package com.malik.lms.auth.service;

import com.malik.lms.auth.dto.request.AuthRequest;
import com.malik.lms.auth.dto.response.AuthResponse;
import com.malik.lms.security.config.PasswordConfig;
import com.malik.lms.security.jwt.JwtUtility;
import com.malik.lms.security.user.CustomUserDetails;
import com.malik.lms.user.entity.User;
import com.malik.lms.user.enums.RoleType;
import com.malik.lms.user.enums.UserStatus;
import com.malik.lms.user.repository.UserRepository;
import com.malik.lms.verification.entity.VerificationToken;
import com.malik.lms.verification.repository.VerificationTokenRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final PasswordConfig passwordConfig;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;

    public AuthService(PasswordConfig passwordEncoder, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, AuthenticationManager authenticationManager, JwtUtility jwtUtility) {
        this.passwordConfig = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
    }

    @Transactional
    public AuthResponse register(AuthRequest authRequest) throws Exception {
        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new Exception("Already exist...");
        }
        User user = new User();
        user.setFullName(authRequest.getFullName());
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordConfig.passwordEncoder().encode((authRequest.getPassword())));
        user.setRole(RoleType.STUDENT);
        user.setStatus(UserStatus.INACTIVE);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        VerificationToken verificationToken = new VerificationToken(savedUser);
        verificationTokenRepository.save(verificationToken);

        String token = verificationToken.getToken().toString();

//  TODO:
//  Send an email containing the token link:
//  "https://yourdomain.com" + tokenString


        return new AuthResponse("Registration successful. Please verify your email.");
    }

    public AuthResponse login(@Valid AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();
        RoleType role = userDetails.getUser().getRole();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());

        String token = jwtUtility.generateToken(email, claims);

        return new AuthResponse(token);

    }
}
