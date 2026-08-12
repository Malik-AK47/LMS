package com.malik.lms.quiz.service;

import com.malik.lms.course.entity.Course;
import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.enrollment.entity.Enrollment;
import com.malik.lms.enrollment.repository.EnrollmentRepository;
import com.malik.lms.quiz.dto.request.QuizAnswerRequest;
import com.malik.lms.quiz.dto.request.SubmitQuizRequest;
import com.malik.lms.quiz.dto.response.QuizAttemptResponse;
import com.malik.lms.quiz.entity.Quiz;
import com.malik.lms.quiz.entity.QuizAttempt;
import com.malik.lms.quiz.repository.QuizAttemptRepository;
import com.malik.lms.quiz.entity.QuizQuestion;
import com.malik.lms.quiz.repository.QuizQuestionRepository;
import com.malik.lms.quiz.repository.QuizRepository;
import com.malik.lms.security.user.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, QuizRepository quizRepository, QuizQuestionRepository quizQuestionRepository, EnrollmentRepository enrollmentRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizRepository = quizRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public QuizAttemptResponse submitQuiz(Long quizId, SubmitQuizRequest submitQuizRequest, Authentication authentication) {
        CustomUserDetails student = (CustomUserDetails) authentication.getPrincipal();
        Long studentId = student.getUser().getId();

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        Course course = quiz.getCourse();

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(studentId, course.getId()).orElseThrow(() -> new RuntimeException("You are not enrolled in this course"));

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByDisplayOrderAsc(quizId);

        if (questions.isEmpty()) {
            throw new RuntimeException("Quiz has no questions");
        }
        
        Map<Long, QuizQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(QuizQuestion::getId, question -> question));

        int correctAnswers = 0;
        Set<Long> submittedQuestionIds = new HashSet<>();

        if (submitQuizRequest.getAnswers() != null) {
            
            for (QuizAnswerRequest answer : submitQuizRequest.getAnswers()) {
                if (!submittedQuestionIds.add(answer.getQuestionId())) {
                    throw new RuntimeException("Duplicate question answer");
                }
                
                QuizQuestion question = questionMap.get(answer.getQuestionId());

                if (question == null) {
                    throw new RuntimeException("Question does not belong to this quiz");
                }

                if (question.getCorrectOption() == answer.getSelectedOption()) {
                    correctAnswers++;
                }
            }
        }

        int totalQuestions = questions.size();
        int score = (correctAnswers * 100) / totalQuestions;

        boolean passed = score >= quiz.getPassingScore();

        long previousAttempts = quizAttemptRepository.countByEnrollmentIdAndQuizId(enrollment.getId(), quizId);
        int attemptNumber = (int) previousAttempts + 1;


        QuizAttempt attempt = new QuizAttempt();
        attempt.setEnrollment(enrollment);
        attempt.setQuiz(quiz);
        attempt.setAttemptNumber(attemptNumber);
        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setSubmittedAt(LocalDateTime.now());

        QuizAttempt savedQuizAttempt = quizAttemptRepository.save(attempt);

        return new QuizAttemptResponse(savedQuizAttempt.getId(), savedQuizAttempt.getAttemptNumber(), savedQuizAttempt.getScore(), savedQuizAttempt.getPassed(), savedQuizAttempt.getSubmittedAt(), passed ? "Quiz passed successfully" : "Quiz failed");
    }
}