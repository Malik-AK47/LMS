package com.malik.lms.quiz.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuizRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    private String description;

    @NotNull(message = "Passing score is required")
    @Min(value = 1, message = "Passing score must be at least 1")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    private Integer passingScore;
}
