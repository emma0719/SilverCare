package com.silvercare.silvercarebackend.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    public enum Role {
        FAMILY, CAREGIVER, ADMIN;
        @JsonCreator
        public static Role fromString(String key) {
            return key == null ? null : Role.valueOf(key.toUpperCase());
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // "en", "zh", "es", "vi"
    @Builder.Default
    @Column(name = "language_preference", length = 10, nullable = false)
    private String languagePreference = "en";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "user_care_recipient",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "care_recipient_id")
    )
    private List<CareRecipient> careRecipients;

    // v9 扩展字段
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "date_of_birth")
    private java.sql.Date dateOfBirth;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "country", length = 64)
    private String country;

    @Column(name = "city", length = 64)
    private String city;

    @Builder.Default
    @Column(name = "preferred_units", length = 16, nullable = false)
    private String preferredUnits = "METRIC"; // METRIC or IMPERIAL

    @Builder.Default
    @Column(name = "theme", length = 16, nullable = false)
    private String theme = "LIGHT"; // LIGHT / DARK / SYSTEM

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "organization", length = 120)
    private String organization;

    @Builder.Default
    @Column(name = "notify_email", nullable = false)
    private Boolean notifyEmail = true;

    @Builder.Default
    @Column(name = "notify_sms", nullable = false)
    private Boolean notifySms = false;

    @Builder.Default
    @Column(name = "notify_push", nullable = false)
    private Boolean notifyPush = true;

    @Builder.Default
    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;

    @Column(name = "last_login_at")
    private java.time.OffsetDateTime lastLoginAt;
}
