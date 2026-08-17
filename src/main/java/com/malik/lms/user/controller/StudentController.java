package com.malik.lms.user.controller;

import com.malik.lms.user.dto.response.StudentEnrollmentResponse;
import com.malik.lms.user.dto.response.StudentResponse;
import com.malik.lms.user.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<StudentResponse> getStudents(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return studentService.getStudents(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studentId}")
    public StudentResponse getStudent(@PathVariable Long studentId) {
        return studentService.getStudent(studentId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studentId}/enrollments")
    public Page<StudentEnrollmentResponse> getStudentEnrollments(@PathVariable Long studentId, @PageableDefault(size = 10, sort = "enrolledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return studentService.getStudentEnrollments(studentId, pageable);
    }
}