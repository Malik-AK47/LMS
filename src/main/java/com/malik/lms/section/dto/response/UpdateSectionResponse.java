package com.malik.lms.section.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSectionResponse {
    private String title;
    private String description;
    private Integer displayOrder;
}
