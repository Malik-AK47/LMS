package com.malik.lms.enrollment.dto.response;


import com.malik.lms.course.enums.DifficultyLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetEnrollmentResponse {

    private Long id;
    private String title;
    private String thumbnail;
    private DifficultyLevel difficultyLevel;
    private String categoryName;
    private LocalDateTime enrolledAt;
}
