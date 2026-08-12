package com.malik.lms.lesson.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseProgressResponse {

    private Long courseId;
    private String courseTitle;
    private long totalLessons;
    private long completedLessons;
    private double progressPercentage;
}