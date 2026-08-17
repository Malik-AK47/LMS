package com.malik.lms.user.dto.request;

import com.malik.lms.user.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorStatusRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;
}