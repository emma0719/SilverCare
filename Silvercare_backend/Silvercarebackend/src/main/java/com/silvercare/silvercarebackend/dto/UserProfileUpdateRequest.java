// src/main/java/com/silvercare/silvercarebackend/dto/UserProfileUpdateRequest.java
package com.silvercare.silvercarebackend.dto;

import lombok.*;
import java.sql.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String languagePreference;

    private String avatarUrl;
    private Date dateOfBirth;
    private String timezone;
    private String country;
    private String city;
    private String preferredUnits; // METRIC/IMPERIAL
    private String theme;          // LIGHT/DARK/SYSTEM
    private String jobTitle;
    private String organization;

    private Boolean notifyEmail;
    private Boolean notifySms;
    private Boolean notifyPush;
    // twoFactorEnabled 视需求通过单独流程开关（推荐不要在这个接口直接改）
}
