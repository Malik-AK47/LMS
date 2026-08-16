package com.malik.lms.quiz.controller;

import com.malik.lms.quiz.dto.request.CreateQuizQuestionRequest;
import com.malik.lms.quiz.dto.request.UpdateQuizQuestionRequest;
import com.malik.lms.quiz.dto.response.CreateQuizQuestionResponse;
import com.malik.lms.quiz.dto.response.QuizQuestionResponse;
import com.malik.lms.quiz.dto.response.UpdateQuizQuestionResponse;
import com.malik.lms.quiz.service.QuizQuestionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class QuizQuestionController {

    private final QuizQuestionService quizQuestionService;

    public QuizQuestionController(QuizQuestionService quizQuestionService) {
        this.quizQuestionService = quizQuestionService;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/instructor/quizzes/{quizId}/questions")
    public CreateQuizQuestionResponse createQuestion(@PathVariable Long quizId, @Valid @RequestBody CreateQuizQuestionRequest request, Authentication authentication) {
        return quizQuestionService.createQuestion(quizId, request, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor/quizzes/{quizId}/questions")
    public List<QuizQuestionResponse> getQuestions(@PathVariable Long quizId, Authentication authentication) {
        return quizQuestionService.getQuestions(quizId, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/instructor/questions/{questionId}")
    public UpdateQuizQuestionResponse updateQuestion(@PathVariable Long questionId, @Valid @RequestBody UpdateQuizQuestionRequest request, Authentication authentication) {
        return quizQuestionService.updateQuestion(questionId, request, authentication);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/instructor/questions/{questionId}")
    public String deleteQuestion(@PathVariable Long questionId, Authentication authentication) {
        return quizQuestionService.deleteQuestion(questionId, authentication);
    }
}
