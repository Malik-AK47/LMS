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
import com.malik.lms.verification.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final PasswordConfig passwordConfig;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;

    private final EmailService emailService;           // To send verification email

    @Value("${app.base-url}")
    private String baseUrl;

    public AuthService( PasswordConfig passwordConfig, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, AuthenticationManager authenticationManager, JwtUtility jwtUtility, EmailService emailService) {
        this.passwordConfig = passwordConfig;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.emailService = emailService;
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

        String verificationLink = baseUrl + "/api/v1/auth/verify?token=" + verificationToken.getToken();

        // send verification email through emailService
        emailService.sendVerificationEmail( savedUser.getEmail(), savedUser.getFullName(), verificationLink);

        return new AuthResponse("Registration successful. Please verify your email.");
    }

    public AuthResponse login(@Valid AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Your account is inactive.");
        }

        String email = userDetails.getUsername();
        RoleType role = userDetails.getUser().getRole();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role.name());

        String token = jwtUtility.generateToken(email, claims);

        return new AuthResponse(token);

    }

    @Transactional
    public AuthResponse verifyEmail(String token) {

        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(UUID.fromString(token))
                        .orElseThrow(() -> new RuntimeException("Invalid verification token."));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Verification token has expired.");
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        return new AuthResponse("Email verified successfully. You can now login.");
    }
}
