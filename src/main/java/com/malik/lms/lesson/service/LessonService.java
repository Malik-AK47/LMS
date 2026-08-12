package com.malik.lms.lesson.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.enrollment.entity.Enrollment;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.lesson.dto.request.CreateLessonRequest;
import com.malik.lms.lesson.dto.request.UpdateLessonRequest;
import com.malik.lms.lesson.dto.response.*;
import com.malik.lms.lesson.entity.Lesson;
import com.malik.lms.lesson.entity.LessonProgress;
import com.malik.lms.lesson.repository.LessonProgressRepository;
import com.malik.lms.lesson.repository.LessonRepository;
import com.malik.lms.section.entity.Section;
import com.malik.lms.section.repository.SectionRepository;
import com.malik.lms.security.user.CustomUserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;

    public LessonService(LessonRepository lessonRepository, SectionRepository sectionRepository, EnrollmentRepository enrollmentRepository, LessonProgressRepository lessonProgressRepository, CourseRepository courseRepository) {
        this.lessonRepository = lessonRepository;
        this.sectionRepository = sectionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.courseRepository = courseRepository;
    }


    public CreateLessonResponse createLesson(Long sectionId, CreateLessonRequest createLessonRequest, Authentication authentication) {

        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Section section = sectionRepository.findByIdAndCourseInstructorId(sectionId, instructorId).orElseThrow(()-> new RuntimeException("not found"));
        Course course = section.getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new RuntimeException("Only draft or rejected courses can be edited");
        }

        if (lessonRepository.existsBySectionIdAndDisplayOrder(sectionId, createLessonRequest.getDisplayOrder())) {
            throw new RuntimeException("Lesson order already exists for this section");
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(createLessonRequest.getTitle());
        lesson.setDescription(createLessonRequest.getDescription());
        lesson.setVideoUrl(createLessonRequest.getVideoUrl());
        lesson.setDurationInMinutes(createLessonRequest.getDurationInMinutes());
        lesson.setDisplayOrder(createLessonRequest.getDisplayOrder());
        lesson.setSection(section);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());

        Lesson savedLesson = lessonRepository.save(lesson);

        return new CreateLessonResponse(savedLesson.getId(), savedLesson.getTitle(), savedLesson.getDisplayOrder(), savedLesson.getSection().getId());
    }

    public List<LessonSummaryResponse> getLessons(Long sectionId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Section section = sectionRepository.findByIdAndCourseInstructorId(sectionId, instructorId).orElseThrow(()-> new RuntimeException("not found"));
        List<Lesson> lessons = section.getLessons();

        return lessons.stream()
                .sorted(Comparator.comparing(Lesson::getDisplayOrder))
                .map(lesson1 -> new LessonSummaryResponse(lesson1.getId(),lesson1.getTitle(), lesson1.getDurationInMinutes(), lesson1.getDisplayOrder()))
                .toList();
    }

    @Transactional
    public UpdateLessonResponse updateLesson(Long lessonId, UpdateLessonRequest request, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Lesson lesson = lessonRepository.findByIdAndSectionCourseInstructorId(lessonId, instructorId).orElseThrow(() -> new RuntimeException("Lesson not found"));
        Course course = lesson.getSection().getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new RuntimeException("Only draft or rejected courses can be edited");
        }
        Integer oldOrder = lesson.getDisplayOrder();
        Integer newOrder = request.getDisplayOrder();

        Long sectionId = lesson.getSection().getId();

        if (!oldOrder.equals(newOrder)) {
            lesson.setDisplayOrder(-1);
            lessonRepository.saveAndFlush(lesson);

            if (newOrder < oldOrder) {
                lessonRepository.shiftLessonsDown(sectionId, newOrder, oldOrder);
            } else {
                lessonRepository.shiftLessonsUp(sectionId, oldOrder, newOrder);
            }
            lesson.setDisplayOrder(newOrder);
        }

        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationInMinutes(request.getDurationInMinutes());
        lesson.setUpdatedAt(LocalDateTime.now());

        Lesson updatedLesson = lessonRepository.save(lesson);

        return new UpdateLessonResponse(updatedLesson.getId(), updatedLesson.getTitle(), updatedLesson.getDisplayOrder(), "Lesson updated successfully");
    }

    @Transactional
    public String deleteLesson(Long lessonId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Lesson lesson = lessonRepository.findByIdAndSectionCourseInstructorId(lessonId, instructorId).orElseThrow(() -> new RuntimeException("Lesson not found"));
        Course course = lesson.getSection().getCourse();

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new RuntimeException("Only draft or rejected courses can be edited");
        }

        Long sectionId = lesson.getSection().getId();
        Integer displayOrder = lesson.getDisplayOrder();

        lessonRepository.delete(lesson);

        lessonRepository.shiftLessonsAfterDelete(sectionId, displayOrder);

        return "Lesson deleted successfully";
    }


    public List<LessonSummaryResponse> getStudentLessons(Long sectionId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new RuntimeException("Section not found"));
        Course course = section.getCourse();

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        if (!enrollmentRepository.existsByUserIdAndCourseId(studentId, course.getId())) {
            throw new RuntimeException("You are not enrolled in this course");
        }

        return section.getLessons()
                .stream()
                .sorted(Comparator.comparing(Lesson::getDisplayOrder))
                .map(lesson ->
                        new LessonSummaryResponse(lesson.getId(), lesson.getTitle(), lesson.getDurationInMinutes(), lesson.getDisplayOrder()))
                .toList();
    }


    @Transactional
    public LessonProgressResponse completeLesson(Long lessonId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));

        Course course = lesson.getSection().getCourse();

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(studentId, course.getId())
                        .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        if (lessonProgressRepository.existsByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)) {
            throw new RuntimeException("Lesson is already completed");
        }

        LessonProgress progress = new LessonProgress();

        progress.setEnrollment(enrollment);
        progress.setLesson(lesson);
        progress.setCompletedAt(LocalDateTime.now());

        LessonProgress saved = lessonProgressRepository.save(progress);

        return new LessonProgressResponse(lesson.getId(), course.getId(), saved.getCompletedAt(), "Lesson completed successfully");
    }


    public CourseProgressResponse getCourseProgress(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(studentId, courseId)
                        .orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        long totalLessons = lessonRepository.countBySectionCourseId(courseId);

        long completedLessons = lessonProgressRepository.countByEnrollmentId(enrollment.getId());

        double progressPercentage = 0;

        if (totalLessons > 0) {
            progressPercentage = (completedLessons * 100.0) / totalLessons;
        }

        return new CourseProgressResponse(course.getId(), course.getTitle(), totalLessons, completedLessons, progressPercentage);
    }
}
