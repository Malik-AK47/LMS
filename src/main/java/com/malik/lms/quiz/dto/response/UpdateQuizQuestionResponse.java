package com.malik.lms.quiz.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuizQuestionResponse {

    private Long id;
    private String questionText;
    private Integer displayOrder;
    private String message;
}