package com.malik.lms.quiz.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentQuizResponse {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer passingScore;
    private List<StudentQuizQuestionResponse> questions;
}
