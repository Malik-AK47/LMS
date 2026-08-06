package com.malik.lms.course.dto.response;

import com.malik.lms.course.enums.CourseStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCourseResponse {
    private Long id;

    private String title;

    private CourseStatus status;
}
