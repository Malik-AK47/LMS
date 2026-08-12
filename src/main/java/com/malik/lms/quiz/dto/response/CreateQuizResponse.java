package com.malik.lms.quiz.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuizResponse {

    private Long id;
    private Long courseId;
    private String title;
    private Integer passingScore;
    private String message;
}
