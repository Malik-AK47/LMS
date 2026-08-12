package com.malik.lms.course.service;

import com.malik.lms.course.dto.response.CourseCompletionResponse;
import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.enrollment.entity.Enrollment;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
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

@Service
public class CourseCompletionService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public CourseCompletionService(EnrollmentRepository enrollmentRepository, LessonRepository lessonRepository, LessonProgressRepository lessonProgressRepository, QuizRepository quizRepository, QuizAttemptRepository quizAttemptRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Transactional
    public CourseCompletionResponse completeCourse(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(studentId, courseId).orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        if (enrollment.getCompletedAt() != null) {
            throw new RuntimeException("Course is already completed");
        }

        Course course = enrollment.getCourse();

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        long totalLessons = lessonRepository.countBySectionCourseId(courseId);

        long completedLessons = lessonProgressRepository.countByEnrollmentId(enrollment.getId());

        if (totalLessons == 0 || completedLessons < totalLessons) {
            throw new RuntimeException("Complete all lessons before completing the course");
        }

        Quiz quiz = quizRepository.findByCourseId(courseId).orElseThrow(() -> new RuntimeException("Course does not have a quiz"));

        quizAttemptRepository.findFirstByEnrollmentIdAndQuizIdAndPassedTrueOrderBySubmittedAtDesc(enrollment.getId(), quiz.getId())
                .orElseThrow(() -> new RuntimeException("You must pass the final quiz before completing the course"));

        enrollment.setCompletedAt(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);

        return new CourseCompletionResponse(saved.getId(), course.getId(), course.getTitle(), saved.getCompletedAt(), "Course completed successfully");
    }
}

