package com.silvercare.silvercarebackend.service;

import com.silvercare.silvercarebackend.domain.User;

public interface UserService {
    User findByUsername(String username);
}

