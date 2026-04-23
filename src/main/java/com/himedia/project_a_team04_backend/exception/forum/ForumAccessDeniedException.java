package com.himedia.project_a_team04_backend.exception.forum;

public class ForumAccessDeniedException extends RuntimeException {
    public ForumAccessDeniedException() {}
    public ForumAccessDeniedException(String msg) {
        super(msg);
    }
}
