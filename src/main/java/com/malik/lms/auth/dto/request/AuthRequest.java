package com.malik.lms.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "Name cant be Empty")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cant be empty")
    private String email;

    @Size(min = 8, max = 15, message = "Password must be at least 8 character")
    private String password;
}
