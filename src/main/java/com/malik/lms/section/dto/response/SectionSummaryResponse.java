package com.malik.lms.section.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionSummaryResponse {

    private Long id;
    private String title;
    private String description;
    private Integer displayOrder;
}
