package com.malik.lms.course.dto.response;

import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.enums.DifficultyLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailsResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private CourseStatus status;
    private DifficultyLevel difficultyLevel;
    private String category;
    private String instructor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
