package com.malik.lms.section.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSectionResponse {
    private Long id;
    private String title;
    private Integer displayOrder;
    private Long courseId;

}
