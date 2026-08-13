package com.malik.lms.certificate.service;

import com.malik.lms.certificate.dto.request.CreateCertificateRequest;
import com.malik.lms.certificate.dto.response.CertificateVerificationResponse;
import com.malik.lms.certificate.dto.response.CreateCertificateResponse;
import com.malik.lms.certificate.dto.response.StudentCertificateResponse;
import com.malik.lms.certificate.entity.Certificate;
import com.malik.lms.certificate.entity.IssuedCertificate;
import com.malik.lms.certificate.repository.CertificateRepository;
import com.malik.lms.certificate.repository.IssuedCertificateRepository;
import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.enrollment.entity.Enrollment;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.security.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final IssuedCertificateRepository issuedCertificateRepository;

    public CertificateService(CertificateRepository certificateRepository, CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, IssuedCertificateRepository issuedCertificateRepository) {
        this.certificateRepository = certificateRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.issuedCertificateRepository = issuedCertificateRepository;
    }

    @Transactional
    public CreateCertificateResponse createCertificate(Long courseId, CreateCertificateRequest request, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(courseId, instructorId).orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new RuntimeException("Only draft or rejected courses can be edited");
        }

        if (certificateRepository.existsByCourseId(courseId)) {
            throw new RuntimeException("Certificate already exists for this course");
        }

        Certificate certificate = new Certificate();

        certificate.setCourse(course);
        certificate.setTitle(request.getTitle());
        certificate.setDescription(request.getDescription());
        certificate.setCreatedAt(LocalDateTime.now());
        certificate.setUpdatedAt(LocalDateTime.now());

        Certificate saved = certificateRepository.save(certificate);

        return new CreateCertificateResponse(saved.getId(), course.getId(), saved.getTitle(), saved.getDescription(), "Certificate created successfully");
    }

    public StudentCertificateResponse getStudentCertificate(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(studentId, courseId).orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        IssuedCertificate issuedCertificate = issuedCertificateRepository.findByEnrollmentId(enrollment.getId()).orElseThrow(() -> new RuntimeException("Certificate has not been issued"));

        Certificate certificate = issuedCertificate.getCertificate();

        Course course = enrollment.getCourse();

        return new StudentCertificateResponse(issuedCertificate.getId(), issuedCertificate.getCertificateNumber(), course.getId(), course.getTitle(), certificate.getTitle(), certificate.getDescription(), issuedCertificate.getIssuedAt());
    }

    public CertificateVerificationResponse verifyCertificate(String certificateNumber) {
        IssuedCertificate issuedCertificate = issuedCertificateRepository.findByCertificateNumber(certificateNumber)
                        .orElseThrow(() -> new RuntimeException("Certificate not found"));

        Certificate certificate = issuedCertificate.getCertificate();

        Course course = certificate.getCourse();

        return new CertificateVerificationResponse(true, issuedCertificate.getCertificateNumber(), course.getTitle(), certificate.getTitle(), issuedCertificate.getIssuedAt());
    }
}