package com.malik.lms.auth.service;

import com.malik.lms.exception.BadRequestException;
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
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public UUIDService(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    public void verifyEmail(String tokenStr) throws Exception {
        UUID UUIDtoken = UUID.fromString(tokenStr);

        VerificationToken verificationToken = verificationTokenRepository.findByToken(UUIDtoken)
                .orElseThrow(()-> new BadRequestException("Invalid verification token..."));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new BadRequestException("Token Expired...");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }
}
