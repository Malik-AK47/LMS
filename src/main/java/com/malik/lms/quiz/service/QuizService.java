package com.malik.lms.quiz.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.repository.CourseRepository;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.quiz.dto.request.CreateQuizRequest;
import com.malik.lms.quiz.dto.response.CreateQuizResponse;
import com.malik.lms.quiz.dto.response.GetQuizResponse;
import com.malik.lms.quiz.dto.response.StudentQuizQuestionResponse;
import com.malik.lms.quiz.dto.response.StudentQuizResponse;
import com.malik.lms.quiz.entity.Quiz;
import com.malik.lms.quiz.repository.QuizQuestionRepository;
import com.malik.lms.quiz.repository.QuizRepository;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public QuizService(QuizRepository quizRepository, CourseRepository courseRepository, QuizQuestionRepository quizQuestionRepository, EnrollmentRepository enrollmentRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public CreateQuizResponse createQuiz(Long courseId, CreateQuizRequest request, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(courseId, instructorId).orElseThrow(()-> new RuntimeException("course not found..."));

        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new RuntimeException("only draft or rejected courses can be edited");
        }

        if (quizRepository.existsByCourseId(courseId)) {
            throw new RuntimeException("quiz already exist...");
        }

        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setPassingScore(request.getPassingScore());
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());

        Quiz savedQuiz = quizRepository.save(quiz);

        return new CreateQuizResponse(savedQuiz.getId(), savedQuiz.getCourse().getId(), savedQuiz.getTitle(), savedQuiz.getPassingScore(), "Quiz created successfully..");
    }

    public GetQuizResponse getQuiz(Long courseId, Authentication authentication) {
        CustomUserDetails instructor = (CustomUserDetails) authentication.getPrincipal();
        Long instructorId = instructor.getUser().getId();

        Course course = courseRepository.findByIdAndInstructorId(courseId, instructorId).orElseThrow(() -> new RuntimeException("Course not found"));

        Quiz quiz = quizRepository.findByCourseId(course.getId()).orElseThrow(() -> new RuntimeException("Quiz not found"));

        long questionCount = quizQuestionRepository.countByQuizId(quiz.getId());

        return new GetQuizResponse(quiz.getId(), course.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getPassingScore(), questionCount, quiz.getCreatedAt(), quiz.getUpdatedAt());
    }

    public StudentQuizResponse getStudentQuiz(Long courseId, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }
        if (!enrollmentRepository.existsByUserIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("You are not enrolled in this course");
        }

        Quiz quiz = quizRepository.findByCourseId(courseId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<StudentQuizQuestionResponse> questions = quizQuestionRepository.findByQuizIdOrderByDisplayOrderAsc(quiz.getId())
                        .stream()
                        .map(question ->
                                new StudentQuizQuestionResponse(question.getId(), question.getQuestionText(), question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(), question.getDisplayOrder()))
                        .toList();

        return new StudentQuizResponse(quiz.getId(), course.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getPassingScore(), questions);
    }
}
