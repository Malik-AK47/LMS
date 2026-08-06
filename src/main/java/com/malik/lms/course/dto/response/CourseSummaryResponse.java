package com.malik.lms.course.dto.response;

import com.malik.lms.course.enums.CourseStatus;
import com.malik.lms.course.enums.DifficultyLevel;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private DifficultyLevel difficultyLevel;
    private String categoryName;
}
