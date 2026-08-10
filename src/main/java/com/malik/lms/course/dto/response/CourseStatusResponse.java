package com.malik.lms.course.dto.response;

import com.malik.lms.course.enums.CourseStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseStatusResponse {

    private Long courseId;
    private CourseStatus status;
    private String message;
}
