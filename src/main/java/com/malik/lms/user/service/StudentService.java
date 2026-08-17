package com.malik.lms.user.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.exception.ResourceNotFoundException;
import com.malik.lms.user.dto.response.StudentEnrollmentResponse;
import com.malik.lms.user.dto.response.StudentResponse;
import com.malik.lms.user.entity.User;
import com.malik.lms.user.enums.RoleType;
import com.malik.lms.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Page<StudentResponse> getStudents(Pageable pageable) {
        return userRepository.findByRole(RoleType.STUDENT, pageable).map(this::toStudentResponse);
    }

    public StudentResponse getStudent(Long studentId) {
        User student = getStudentUser(studentId);
        return toStudentResponse(student);
    }

    public Page<StudentEnrollmentResponse> getStudentEnrollments(Long studentId, Pageable pageable) {
        getStudentUser(studentId);
        return enrollmentRepository.findByUserId(studentId, pageable).map(enrollment -> {
            Course course = enrollment.getCourse();
            return new StudentEnrollmentResponse(enrollment.getId(), course.getId(), course.getTitle(), enrollment.getEnrolledAt(), enrollment.getCompletedAt());
        });
    }

    private User getStudentUser(Long studentId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getRole() != RoleType.STUDENT) {
            throw new ResourceNotFoundException("Student not found");
        }
        return student;
    }

    private StudentResponse toStudentResponse(User student) {
        return new StudentResponse(student.getId(), student.getFullName(), student.getEmail(), student.getStatus(), student.isEmailVerified(), student.getCreatedAt());
    }
}