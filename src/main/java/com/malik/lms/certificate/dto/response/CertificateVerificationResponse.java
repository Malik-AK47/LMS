package com.malik.lms.certificate.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateVerificationResponse {

    private boolean valid;
    private String certificateNumber;
    private String courseTitle;
    private String certificateTitle;
    private LocalDateTime issuedAt;
}