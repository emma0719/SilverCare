package com.silvercare.silvercarebackend.web;

import com.silvercare.silvercarebackend.dto.UserProfileDTO;
import com.silvercare.silvercarebackend.dto.UserProfileUpdateRequest;
import com.silvercare.silvercarebackend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users/me", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8082") // 你的前端端口
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping
    public UserProfileDTO me() {
        return userProfileService.getMe();
    }

    @PutMapping(consumes = "application/json")
    public UserProfileDTO update(@RequestBody UserProfileUpdateRequest request) {
        return userProfileService.updateMe(request);
    }
}
