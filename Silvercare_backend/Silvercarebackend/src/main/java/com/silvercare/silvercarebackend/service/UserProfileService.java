package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.dto.UserProfileDTO;
import com.silvercare.silvercarebackend.dto.UserProfileUpdateRequest;

public interface UserProfileService {
    UserProfileDTO getMe();
    UserProfileDTO updateMe(UserProfileUpdateRequest request);
}
