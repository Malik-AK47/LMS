package com.malik.lms.section.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSectionRequest {
    @NotBlank(message = "title cant be empty...")
    private String title;

    private String description;

    @NotNull(message = "Order should not null")
    @Positive(message = "Order should be positive")
    private Integer displayOrder;
}
