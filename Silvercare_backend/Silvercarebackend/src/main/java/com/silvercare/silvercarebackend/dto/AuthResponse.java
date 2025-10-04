package com.silvercare.silvercarebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String message;
    private String preferredLanguage;
}
