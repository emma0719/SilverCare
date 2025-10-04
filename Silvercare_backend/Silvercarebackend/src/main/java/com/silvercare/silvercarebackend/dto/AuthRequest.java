package com.silvercare.silvercarebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AuthRequest {

    @NotBlank(message = "{auth.username.required}")
    @Size(max = 100, message = "{validation.field.tooLong}")
    private String username;

    @NotBlank(message = "{auth.password.required}")
    @Size(min = 6, max = 128, message = "{validation.password.size}")
    private String password;
}
