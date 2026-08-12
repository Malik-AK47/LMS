package com.malik.lms.lesson.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressResponse {

    private Long lessonId;
    private Long courseId;
    private LocalDateTime completedAt;
    private String message;
}
