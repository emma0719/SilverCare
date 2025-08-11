package com.silvercare.silvercarebackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CareRecipientCreateReq(
        @NotBlank @Size(max = 191) String name,
        @Size(max = 16) String gender,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dob,
        @Size(max = 64) String roomNo,
        String allergies,
        String conditions
) {}
