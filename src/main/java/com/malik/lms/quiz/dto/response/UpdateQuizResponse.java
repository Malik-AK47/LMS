package com.malik.lms.quiz.dto.response;

public record UpdateQuizResponse(
        Long quizId,
        String title,
        Integer passingScore,
        String message
) {
}