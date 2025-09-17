package com.silvercare.silvercarebackend.dto;

import com.silvercare.silvercarebackend.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private User.Role role;
    private String languagePreference;
    private String fullName;

}
