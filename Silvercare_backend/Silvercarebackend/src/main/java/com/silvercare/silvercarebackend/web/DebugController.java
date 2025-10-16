package com.silvercare.silvercarebackend.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/ping")
    public Map<String,Object> ping() {
        return Map.of("ok", true);
    }

    @GetMapping("/me")
    public Map<String,Object> me(Authentication auth) {
        boolean authed = auth != null && auth.isAuthenticated();
        return Map.of(
                "authenticated", authed,
                "name", authed ? auth.getName() : null,
                "authorities", authed ? auth.getAuthorities() : null
        );
    }
}
