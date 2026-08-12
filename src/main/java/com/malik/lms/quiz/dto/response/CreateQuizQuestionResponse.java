package com.malik.lms.quiz.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuizQuestionResponse {

    private Long id;
    private Long quizId;
    private String questionText;
    private Integer displayOrder;
    private String message;
}
