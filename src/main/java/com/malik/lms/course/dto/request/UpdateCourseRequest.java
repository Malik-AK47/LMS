package com.malik.lms.course.dto.request;

import com.malik.lms.course.enums.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCourseRequest {

    @NotBlank(message = "title cant be empty...")
    private String title;

    @NotBlank(message = "description cant be empty...")
    private String description;

    @NotNull(message = "Price should not null")
    @Positive(message = "Price should be positive")
    private BigDecimal price;

    @NotNull(message = "difficulty should not null")
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "CategoryId should not null")
    private Long categoryId;

    @NotBlank(message = "thumbnail should not be null...")
    private String thumbnail;
}
