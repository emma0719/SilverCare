// src/main/java/com/silvercare/silvercarebackend/dto/UserProfileDTO.java
package com.silvercare.silvercarebackend.dto;

import com.silvercare.silvercarebackend.domain.User.Role;
import lombok.*;

import java.time.OffsetDateTime;
import java.sql.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private Boolean active;
    private String languagePreference;

    // 新增展示字段
    private String avatarUrl;
    private Date dateOfBirth;
    private String timezone;
    private String country;
    private String city;
    private String preferredUnits; // METRIC/IMPERIAL
    private String theme;          // LIGHT/DARK/SYSTEM
    private String jobTitle;
    private String organization;

    // 通知/安全（只读展示，不在前端直接改两步验证开关也可以）
    private Boolean notifyEmail;
    private Boolean notifySms;
    private Boolean notifyPush;
    private Boolean twoFactorEnabled;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastLoginAt;
}
