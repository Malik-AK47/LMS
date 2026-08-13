package com.malik.lms.certificate.service;

import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import com.malik.lms.certificate.entity.Certificate;
import com.malik.lms.certificate.entity.IssuedCertificate;
import com.malik.lms.certificate.repository.IssuedCertificateRepository;
import com.malik.lms.course.entity.Course;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CertificatePdfService {
    private final IssuedCertificateRepository issuedCertificateRepository;

    public CertificatePdfService(IssuedCertificateRepository issuedCertificateRepository) {
        this.issuedCertificateRepository = issuedCertificateRepository;
    }

    public byte[] generateCertificatePdf(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        IssuedCertificate issuedCertificate = issuedCertificateRepository.findByEnrollmentUserIdAndEnrollmentCourseId(studentId, courseId)
                        .orElseThrow(() -> new RuntimeException("Certificate not found"));

        Certificate certificate = issuedCertificate.getCertificate();

        Course course = certificate.getCourse();

        String studentName = issuedCertificate.getEnrollment().getUser().getFullName();

        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);

            Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            Paragraph studentParagraph  = new Paragraph(studentName, nameFont);
            studentParagraph .setAlignment(Element.ALIGN_CENTER);
            document.add(studentParagraph);

            document.add(new Paragraph(" "));

            Paragraph completionText = new Paragraph("has successfully completed", normalFont);
            completionText.setAlignment(Element.ALIGN_CENTER);
            document.add(completionText);

            document.add(new Paragraph(" "));

            Paragraph courseTitle = new Paragraph(course.getTitle(), nameFont);
            courseTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(courseTitle);

            document.add(new Paragraph(" "));

            Paragraph certificateNumber = new Paragraph("Certificate No: " + issuedCertificate.getCertificateNumber(), normalFont);
            certificateNumber.setAlignment(Element.ALIGN_CENTER);
            document.add(certificateNumber);

            Paragraph issuedDate = new Paragraph("Issued: " + issuedCertificate.getIssuedAt().toLocalDate(), normalFont);
            issuedDate.setAlignment(Element.ALIGN_CENTER);
            document.add(issuedDate);
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate PDF", e);
        }
    }
}