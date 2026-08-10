package com.malik.lms.lesson.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLessonResponse {

    private Long id;
    private String title;
    private Integer displayOrder;
    private Long sectionId;
}