package com.malik.lms.quiz.dto.response;

import com.malik.lms.quiz.enums.CorrectOption;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionResponse {

    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private CorrectOption correctOption;
    private Integer displayOrder;
}