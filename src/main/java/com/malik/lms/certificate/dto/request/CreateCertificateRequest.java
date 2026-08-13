package com.malik.lms.certificate.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCertificateRequest {

    @NotBlank(message = "Certificate title is required")
    private String title;

    private String description;
}