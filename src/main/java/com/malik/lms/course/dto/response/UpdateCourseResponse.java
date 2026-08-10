package com.malik.lms.course.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCourseResponse {

        private Long id;

        private String title;

        private String message;
}
