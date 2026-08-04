package com.malik.lms.verification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String fullName, String verificationLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Verify your email");

        message.setText("""
                Hello %s,

                Welcome to LMS!

                Please click the link below to verify your email address:

                %s

                This link will expire in 1 hour.

                If you didn't create this account, you can safely ignore this email.

                Regards,
                LMS Team
                """.formatted(fullName, verificationLink));

        mailSender.send(message);
    }
}
