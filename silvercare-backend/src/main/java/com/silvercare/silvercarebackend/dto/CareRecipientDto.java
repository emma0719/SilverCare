package com.silvercare.silvercarebackend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CareRecipientDto(
        Long id,
        String name,
        String gender,
        LocalDate dob,
        String roomNo,
        String allergies,
        String conditions,
        LocalDateTime createdAt
) {}
