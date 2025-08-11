package com.silvercare.silvercarebackend.web.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
