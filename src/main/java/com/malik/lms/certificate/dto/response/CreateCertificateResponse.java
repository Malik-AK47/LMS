package com.malik.lms.certificate.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCertificateResponse {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private String message;
}