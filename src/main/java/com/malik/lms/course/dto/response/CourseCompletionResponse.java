package com.malik.lms.course.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCompletionResponse {

    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime completedAt;
    private String message;
}