package com.malik.lms.certificate.entity;

import com.malik.lms.enrollment.entity.Enrollment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "issued_certificates", uniqueConstraints = {@UniqueConstraint(name = "uk_issued_certificate_enrollment", columnNames = "enrollment_id")})
public class IssuedCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Column(nullable = false, unique = true)
    private String certificateNumber;

    @Column(nullable = false)
    private LocalDateTime issuedAt;
}