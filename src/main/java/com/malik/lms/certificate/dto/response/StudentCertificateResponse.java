package com.malik.lms.certificate.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCertificateResponse {

    private Long id;
    private String certificateNumber;
    private Long courseId;
    private String courseTitle;
    private String certificateTitle;
    private String certificateDescription;
    private LocalDateTime issuedAt;
}