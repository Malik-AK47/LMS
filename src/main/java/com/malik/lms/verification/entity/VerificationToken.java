package com.malik.lms.verification.entity;

import com.malik.lms.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private UUID token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public VerificationToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID();
        this.expiresAt = LocalDateTime.now().plusHours(1);
    }

    public VerificationToken() {
    }
}
