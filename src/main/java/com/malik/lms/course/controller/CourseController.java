package com.malik.lms.course.controller;

import com.malik.lms.course.dto.request.CreateCourseRequest;
import com.malik.lms.course.dto.response.CourseSummaryResponse;
import com.malik.lms.course.dto.response.CreateCourseResponse;
import com.malik.lms.course.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/create")
    public CreateCourseResponse createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest, Authentication authentication) {
        return courseService.createCourse(createCourseRequest, authentication);
    }

    @GetMapping("/courses")
    public Page<CourseSummaryResponse> getPublishedCourses(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return courseService.getPublishedCourses(pageable);
    }
}
