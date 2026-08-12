package com.malik.lms.course.controller;

import com.malik.lms.course.dto.request.RejectCourseRequest;
import com.malik.lms.course.dto.request.UpdateCourseRequest;
import com.malik.lms.course.dto.response.*;
import com.malik.lms.course.dto.request.CreateCourseRequest;
import com.malik.lms.course.service.CourseCompletionService;
import com.malik.lms.course.service.CourseService;
import com.malik.lms.lesson.dto.request.CreateLessonRequest;
import com.malik.lms.lesson.dto.request.UpdateLessonRequest;
import com.malik.lms.lesson.dto.response.*;
import com.malik.lms.lesson.service.LessonService;
import com.malik.lms.section.dto.request.CreateSectionRequest;
import com.malik.lms.section.dto.request.UpdateSectionRequest;
import com.malik.lms.section.dto.response.CreateSectionResponse;
import com.malik.lms.section.dto.response.SectionSummaryResponse;
import com.malik.lms.section.dto.response.UpdateSectionResponse;
import com.malik.lms.section.service.SectionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;
    private final SectionService sectionService;
    private final LessonService lessonService;
    private final CourseCompletionService courseCompletionService;

    public CourseController(CourseService courseService, SectionService sectionService, LessonService lessonService, CourseCompletionService courseCompletionService) {
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.lessonService = lessonService;
        this.courseCompletionService = courseCompletionService;
    }

    @GetMapping("/courses")
    public Page<CourseSummaryResponse> getPublishedCourses(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return courseService.getPublishedCourses(pageable);
    }

    @GetMapping("/courses/{id}")
    public CourseDetailsResponse getCourseDetails(@PathVariable Long id) {
        return courseService.getCourseDetails(id);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/create")
    public CreateCourseResponse createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest, Authentication authentication) {
        return courseService.createCourse(createCourseRequest, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor/courses")
    public Page<InstructorCourseSummaryResponse> getAllInstructorCourses(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        return courseService.getAllInstructorCourses(pageable, authentication);
    }


    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/instructor/courses/{id}")
    public UpdateCourseResponse updateCourse(@Valid @RequestBody UpdateCourseRequest updateCourseRequest, @PathVariable Long id, Authentication authentication) {
        return courseService.updateCourse(updateCourseRequest, id, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/courses/{courseId}/submit-review")
    public CourseStatusResponse submitForReview(@PathVariable Long courseId, Authentication authentication) {
        return courseService.submitForReview(courseId, authentication);
    }


    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/courses/{courseId}/sections")
    public CreateSectionResponse createSection(@PathVariable Long courseId, @Valid @RequestBody CreateSectionRequest createSectionRequest, Authentication authentication) {
        return sectionService.createSection(courseId, createSectionRequest, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor/courses/{courseId}/sections")
    public List<SectionSummaryResponse> getSection(@PathVariable Long courseId, Authentication authentication) {
        return sectionService.getSections(courseId, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/instructor/sections/{sectionId}")
    public UpdateSectionResponse updateSection(@Valid @RequestBody UpdateSectionRequest updateSectionRequest, @PathVariable Long sectionId, Authentication authentication) {
        return sectionService.updateSection(updateSectionRequest, sectionId, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/sections/{sectionId}/lessons")
    public CreateLessonResponse createLesson(@PathVariable Long sectionId, @Valid @RequestBody CreateLessonRequest createLessonRequest, Authentication authentication) {
        return lessonService.createLesson(sectionId, createLessonRequest, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor/sections/{sectionId}/lessons")
    public List<LessonSummaryResponse> getLessons(@PathVariable Long sectionId, Authentication authentication) {
        return lessonService.getLessons(sectionId, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/instructor/lessons/{lessonId}")
    public UpdateLessonResponse updateLesson(@PathVariable Long lessonId, @Valid @RequestBody UpdateLessonRequest request, Authentication authentication) {
        return lessonService.updateLesson(lessonId, request, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/instructor/lessons/{lessonId}")
    public String deleteLesson(@PathVariable Long lessonId, Authentication authentication) {
        return lessonService.deleteLesson(lessonId, authentication);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/courses/pending-review")
    public Page<AdminCourseReviewResponse> getPendingCourses(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return courseService.getPendingCourses(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/courses/{courseId}/publish")
    public CourseStatusResponse publishCourse(@PathVariable Long courseId) {
        return courseService.publishCourse(courseId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/courses/{courseId}/reject")
    public CourseStatusResponse rejectCourse(@PathVariable Long courseId, @Valid @RequestBody RejectCourseRequest request) {
        return courseService.rejectCourse(courseId, request);
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses/{courseId}/sections")
    public List<SectionSummaryResponse> getStudentSections(@PathVariable Long courseId, Authentication authentication) {
        return sectionService.getStudentSections(courseId, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/sections/{sectionId}/lessons")
    public List<LessonSummaryResponse> getStudentLessons(@PathVariable Long sectionId, Authentication authentication) {
        return lessonService.getStudentLessons(sectionId, authentication);
    }


    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/student/lessons/{lessonId}/complete")
    public LessonProgressResponse completeLesson(@PathVariable Long lessonId, Authentication authentication) {
        return lessonService.completeLesson(lessonId, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses/{courseId}/progress")
    public CourseProgressResponse getCourseProgress(@PathVariable Long courseId, Authentication authentication) {
        return lessonService.getCourseProgress(courseId, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/student/courses/{courseId}/complete")
    public CourseCompletionResponse completeCourse(@PathVariable Long courseId, Authentication authentication) {
        return courseCompletionService.completeCourse(courseId, authentication);
    }
}
