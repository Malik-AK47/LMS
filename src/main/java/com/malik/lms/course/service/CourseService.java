package com.malik.lms.course.service;

import com.malik.lms.category.entity.Category;
import com.malik.lms.category.repository.CategoryRepository;
import com.malik.lms.course.dto.request.CreateCourseRequest;
import com.malik.lms.course.dto.response.CourseSummaryResponse;
import com.malik.lms.course.dto.response.CreateCourseResponse;
import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    public CreateCourseResponse createCourse(CreateCourseRequest createCourseRequest, Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Category category = categoryRepository.findById(createCourseRequest.getCategoryId()).orElseThrow(()-> new RuntimeException("not found..."));

        Course course = new Course();
        course.setTitle(createCourseRequest.getTitle());
        course.setDescription(createCourseRequest.getDescription());
        course.setPrice(createCourseRequest.getPrice());
        course.setDifficultyLevel(createCourseRequest.getDifficultyLevel());
        course.setCategory(category);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setStatus(CourseStatus.DRAFT);
        course.setInstructor(instructor.getUser());

        Course savedCourse = courseRepository.save(course);

        return new CreateCourseResponse(savedCourse.getId(), savedCourse.getTitle(), savedCourse.getStatus());

    }

    public Page<CourseSummaryResponse> getPublishedCourses(Pageable pageable) {

        Page<Course> courses = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);

        return courses.map(course -> new CourseSummaryResponse(course.getId(), course.getTitle(), course.getPrice(), course.getDifficultyLevel(), course.getCategory().getName()));
    }
}
