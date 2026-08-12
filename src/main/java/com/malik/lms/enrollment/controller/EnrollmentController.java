package com.malik.lms.enrollment.controller;

import com.malik.lms.enrollment.dto.response.EnrollmentResponse;
import com.malik.lms.enrollment.dto.response.GetEnrollmentResponse;
import com.malik.lms.enrollment.service.EnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/student/courses/{courseId}/enroll")
    public EnrollmentResponse enrollInCourse(@PathVariable Long courseId, Authentication authentication) {
        return enrollmentService.enrollInCourse(courseId, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses")
    public Page<GetEnrollmentResponse> getEnrolledCourse(@PageableDefault(size = 10, sort = "enrolledAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        return enrollmentService.getEnrolledCourses(pageable, authentication);
    }
}
