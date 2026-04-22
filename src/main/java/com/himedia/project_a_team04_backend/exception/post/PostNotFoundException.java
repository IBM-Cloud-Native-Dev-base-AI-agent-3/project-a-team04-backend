package com.himedia.project_a_team04_backend.exception.post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
