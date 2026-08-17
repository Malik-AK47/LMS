package com.malik.lms.user.dto.response;

import com.malik.lms.user.enums.RoleType;
import com.malik.lms.user.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorResponse {

    private Long id;
    private String fullName;
    private String email;
    private RoleType role;
    private UserStatus status;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}