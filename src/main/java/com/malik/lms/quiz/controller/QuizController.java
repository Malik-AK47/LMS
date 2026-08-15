package com.malik.lms.quiz.controller;

import com.malik.lms.quiz.dto.request.CreateQuizRequest;
import com.malik.lms.quiz.dto.request.SubmitQuizRequest;
import com.malik.lms.quiz.dto.response.CreateQuizResponse;
import com.malik.lms.quiz.dto.response.GetQuizResponse;
import com.malik.lms.quiz.dto.response.QuizAttemptResponse;
import com.malik.lms.quiz.dto.response.StudentQuizResponse;
import com.malik.lms.quiz.service.QuizAttemptService;
import com.malik.lms.quiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class QuizController {

    private final QuizService quizService;
    private final QuizAttemptService quizAttemptService;

    public QuizController(QuizService quizService, QuizAttemptService quizAttemptService) {
        this.quizService = quizService;
        this.quizAttemptService = quizAttemptService;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/courses/{courseId}/quiz")
    public CreateQuizResponse createQuiz(@PathVariable Long courseId, @Valid @RequestBody CreateQuizRequest request, Authentication authentication) {
        return quizService.createQuiz(courseId, request, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor/courses/{courseId}/quiz")
    public GetQuizResponse getQuiz(@PathVariable Long courseId, Authentication authentication) {
        return quizService.getQuiz(courseId, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses/{courseId}/quiz")
    public StudentQuizResponse getStudentQuiz(@PathVariable Long courseId, Authentication authentication) {
        return quizService.getStudentQuiz(courseId, authentication);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/student/quizzes/{quizId}/submit")
    public QuizAttemptResponse submitQuiz(@PathVariable Long quizId, @Valid @RequestBody SubmitQuizRequest request, Authentication authentication) {
        return quizAttemptService.submitQuiz(quizId, request, authentication);
    }
}
