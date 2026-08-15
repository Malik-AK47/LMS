package com.malik.lms.certificate.controller;

import com.malik.lms.certificate.dto.request.CreateCertificateRequest;
import com.malik.lms.certificate.dto.response.CertificateVerificationResponse;
import com.malik.lms.certificate.dto.response.CreateCertificateResponse;
import com.malik.lms.certificate.dto.response.StudentCertificateResponse;
import com.malik.lms.certificate.service.CertificatePdfService;
import com.malik.lms.certificate.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificatePdfService certificatePdfService;

    public CertificateController(CertificateService certificateService, CertificatePdfService certificatePdfService) {
        this.certificateService = certificateService;
        this.certificatePdfService = certificatePdfService;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/courses/{courseId}/certificate")
    public CreateCertificateResponse createCertificate(@PathVariable Long courseId, @Valid @RequestBody CreateCertificateRequest request, Authentication authentication) {
        return certificateService.createCertificate(courseId, request, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses/{courseId}/certificate")
    public StudentCertificateResponse getStudentCertificate(@PathVariable Long courseId, Authentication authentication) {
        return certificateService.getStudentCertificate(courseId, authentication);
    }

    @GetMapping("/certificates/{certificateNumber}/verify")
    public CertificateVerificationResponse verifyCertificate(@PathVariable String certificateNumber) {
        return certificateService.verifyCertificate(certificateNumber);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses/{courseId}/certificate/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long courseId, Authentication authentication) {
        byte[] pdf = certificatePdfService.generateCertificatePdf(courseId, authentication);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
