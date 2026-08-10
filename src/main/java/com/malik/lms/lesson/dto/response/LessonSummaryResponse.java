package com.malik.lms.lesson.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonSummaryResponse {
    private Long id;
    private String title;
    private Integer durationInMinutes;
    private Integer displayOrder;
}
