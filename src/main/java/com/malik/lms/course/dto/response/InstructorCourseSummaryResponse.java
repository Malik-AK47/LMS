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
public class InstructorCourseSummaryResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private DifficultyLevel difficultyLevel;
    private String categoryName;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private String thumbnail;
}
