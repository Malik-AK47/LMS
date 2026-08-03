package com.malik.lms.auth.service;

import com.malik.lms.user.entity.User;
import com.malik.lms.user.enums.UserStatus;
import com.malik.lms.user.repository.UserRepository;
import com.malik.lms.verification.entity.VerificationToken;
import com.malik.lms.verification.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UUIDService {
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public void verifyEmail(String tokenStr) throws Exception {
        UUID UUIDtoken = UUID.fromString(tokenStr);

        VerificationToken verificationToken = verificationTokenRepository.findByToken(UUIDtoken)
                .orElseThrow(()-> new Exception("Invalid token..."));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new Exception("Token Expired...");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }
}
