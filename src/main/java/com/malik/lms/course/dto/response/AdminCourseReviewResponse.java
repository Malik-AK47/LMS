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
public class AdminCourseReviewResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private DifficultyLevel difficultyLevel;
    private String categoryName;
    private String instructorName;
    private CourseStatus status;
    private LocalDateTime createdAt;
}
