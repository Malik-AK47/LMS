package com.malik.lms.quiz.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuizRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer passingScore;
}