package com.malik.lms.quiz.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetQuizResponse {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer passingScore;
    private Long questionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}