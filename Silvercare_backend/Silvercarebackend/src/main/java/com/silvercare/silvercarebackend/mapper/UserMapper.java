package com.silvercare.silvercarebackend.mapper;

import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.dto.UserProfileDTO;
import com.silvercare.silvercarebackend.dto.UserProfileUpdateRequest;

public class UserMapper {

    public static UserProfileDTO toProfileDTO(User u) {
        if (u == null) return null;
        return UserProfileDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .role(u.getRole())
                .active(u.getActive())
                .languagePreference(u.getLanguagePreference())
                .avatarUrl(u.getAvatarUrl())
                .dateOfBirth(u.getDateOfBirth())
                .timezone(u.getTimezone())
                .country(u.getCountry())
                .city(u.getCity())
                .preferredUnits(u.getPreferredUnits())
                .theme(u.getTheme())
                .jobTitle(u.getJobTitle())
                .organization(u.getOrganization())
                .notifyEmail(u.getNotifyEmail())
                .notifySms(u.getNotifySms())
                .notifyPush(u.getNotifyPush())
                .twoFactorEnabled(u.getTwoFactorEnabled())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .lastLoginAt(u.getLastLoginAt())
                .build();
    }

    public static void applyUpdate(User u, UserProfileUpdateRequest r) {
        if (r == null || u == null) return;

        if (r.getFullName() != null) u.setFullName(r.getFullName());
        if (r.getPhoneNumber() != null) u.setPhoneNumber(r.getPhoneNumber());
        if (r.getEmail() != null) u.setEmail(r.getEmail());
        if (r.getLanguagePreference() != null) u.setLanguagePreference(r.getLanguagePreference());

        if (r.getAvatarUrl() != null) u.setAvatarUrl(r.getAvatarUrl());
        if (r.getDateOfBirth() != null) u.setDateOfBirth(r.getDateOfBirth());
        if (r.getTimezone() != null) u.setTimezone(r.getTimezone());
        if (r.getCountry() != null) u.setCountry(r.getCountry());
        if (r.getCity() != null) u.setCity(r.getCity());
        if (r.getPreferredUnits() != null) u.setPreferredUnits(r.getPreferredUnits());
        if (r.getTheme() != null) u.setTheme(r.getTheme());
        if (r.getJobTitle() != null) u.setJobTitle(r.getJobTitle());
        if (r.getOrganization() != null) u.setOrganization(r.getOrganization());

        if (r.getNotifyEmail() != null) u.setNotifyEmail(r.getNotifyEmail());
        if (r.getNotifySms() != null) u.setNotifySms(r.getNotifySms());
        if (r.getNotifyPush() != null) u.setNotifyPush(r.getNotifyPush());
    }
}
