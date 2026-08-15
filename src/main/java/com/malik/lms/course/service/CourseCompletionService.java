package com.malik.lms.course.service;

import com.malik.lms.certificate.entity.Certificate;
import com.malik.lms.certificate.entity.IssuedCertificate;
import com.malik.lms.certificate.repository.CertificateRepository;
import com.malik.lms.certificate.repository.IssuedCertificateRepository;
import com.malik.lms.course.dto.response.CourseCompletionResponse;
import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.enrollment.entity.Enrollment;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.exception.BadRequestException;
import com.malik.lms.exception.ConflictException;
import com.malik.lms.exception.ForbiddenException;
import com.malik.lms.lesson.repository.LessonProgressRepository;
import com.malik.lms.lesson.repository.LessonRepository;
import com.malik.lms.quiz.entity.Quiz;
import com.malik.lms.quiz.repository.QuizAttemptRepository;
import com.malik.lms.quiz.repository.QuizRepository;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CourseCompletionService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CertificateRepository certificateRepository;
    private final IssuedCertificateRepository issuedCertificateRepository;

    public CourseCompletionService(EnrollmentRepository enrollmentRepository, LessonRepository lessonRepository, LessonProgressRepository lessonProgressRepository, QuizRepository quizRepository, QuizAttemptRepository quizAttemptRepository, CertificateRepository certificateRepository, IssuedCertificateRepository issuedCertificateRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.certificateRepository = certificateRepository;
        this.issuedCertificateRepository = issuedCertificateRepository;
    }

    @Transactional
    public CourseCompletionResponse completeCourse(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(studentId, courseId).orElseThrow(() -> new ForbiddenException("You are not enrolled in this course"));

        if (enrollment.getCompletedAt() != null) {
            throw new ConflictException("Course is already completed");
        }

        Course course = enrollment.getCourse();

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ForbiddenException("Course is not available");
        }

        long totalLessons = lessonRepository.countBySectionCourseId(courseId);

        long completedLessons = lessonProgressRepository.countByEnrollmentId(enrollment.getId());

        if (totalLessons == 0 || completedLessons < totalLessons) {
            throw new BadRequestException("Complete all lessons before completing the course");
        }

        Quiz quiz = quizRepository.findByCourseId(courseId).orElseThrow(() -> new BadRequestException("Course does not have a quiz"));

        quizAttemptRepository.findFirstByEnrollmentIdAndQuizIdAndPassedTrueOrderBySubmittedAtDesc(enrollment.getId(), quiz.getId())
                .orElseThrow(() -> new BadRequestException("You must pass the final quiz before completing the course"));

        enrollment.setCompletedAt(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);

        Certificate certificate = certificateRepository.findByCourseId(courseId).orElseThrow(() -> new BadRequestException("Certificate not configured for this course"));

        if (issuedCertificateRepository.existsByEnrollmentId(enrollment.getId())) {
            throw new ConflictException("Certificate already issued");
        }

        IssuedCertificate issuedCertificate = new IssuedCertificate();

        issuedCertificate.setEnrollment(saved);
        issuedCertificate.setCertificate(certificate);
        issuedCertificate.setCertificateNumber("CERT-" + UUID.randomUUID());
        issuedCertificate.setIssuedAt(LocalDateTime.now());

        issuedCertificateRepository.save(issuedCertificate);

        return new CourseCompletionResponse(saved.getId(), course.getId(), course.getTitle(), saved.getCompletedAt(), "Course completed and certificate issued successfully");
    }
}

