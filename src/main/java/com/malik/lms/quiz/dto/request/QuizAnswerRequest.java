package com.malik.lms.quiz.dto.request;

import com.malik.lms.quiz.enums.CorrectOption;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerRequest {

    @NotNull(message = "Question id is required")
    private Long questionId;

    @NotNull(message = "Selected option is required")
    private CorrectOption selectedOption;
}