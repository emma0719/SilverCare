package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.User;
import com.silvercare.silvercarebackend.dto.UserProfileDTO;
import com.silvercare.silvercarebackend.dto.UserProfileUpdateRequest;
import com.silvercare.silvercarebackend.mapper.UserMapper;
import com.silvercare.silvercarebackend.repository.UserRepository;
import com.silvercare.silvercarebackend.security.AuthenticationFacade;
import com.silvercare.silvercarebackend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final AuthenticationFacade auth;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getMe() {
        String username = auth.currentUsername();
        if (username == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return UserMapper.toProfileDTO(user);
    }

    @Override
    @Transactional
    public UserProfileDTO updateMe(UserProfileUpdateRequest request) {
        String username = auth.currentUsername();
        if (username == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 不允许通过此接口修改用户名/角色/密码/active 等敏感字段
        UserMapper.applyUpdate(user, request);
        User saved = userRepository.save(user);
        return UserMapper.toProfileDTO(saved);
    }
}
