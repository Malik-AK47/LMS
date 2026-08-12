package com.malik.lms.quiz.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptResponse {

    private Long attemptId;
    private Integer attemptNumber;
    private Integer score;
    private Boolean passed;
    private LocalDateTime submittedAt;
    private String message;
}