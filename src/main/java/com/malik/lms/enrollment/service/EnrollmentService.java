package com.malik.lms.enrollment.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.enrollment.dto.response.EnrollmentResponse;
import com.malik.lms.enrollment.dto.response.GetEnrollmentResponse;
import com.malik.lms.enrollment.entity.Enrollment;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.exception.BadRequestException;
import com.malik.lms.exception.ConflictException;
import com.malik.lms.exception.ResourceNotFoundException;
import com.malik.lms.security.user.CustomUserDetails;
import com.malik.lms.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public EnrollmentResponse enrollInCourse(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        User user = student.getUser();

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Only published courses can be enrolled");
        }


        // TODO: fix when payment feature is added...
        if (course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Payment is required for this course");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new ConflictException("You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return new EnrollmentResponse(savedEnrollment.getId(), course.getId(), course.getTitle(), savedEnrollment.getEnrolledAt(), "Successfully enrolled in course");
    }

    public Page<GetEnrollmentResponse> getEnrolledCourses(Pageable pageable, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long userId = student.getUser().getId();

        Page<Enrollment> enrollments = enrollmentRepository.findByUserId(userId, pageable);

        return enrollments.map(enrollment -> {
            Course course = enrollment.getCourse();

            return new GetEnrollmentResponse(course.getId(), course.getTitle(), course.getThumbnail(), course.getDifficultyLevel(), course.getCategory().getName(), enrollment.getEnrolledAt());
        });
    }
}
