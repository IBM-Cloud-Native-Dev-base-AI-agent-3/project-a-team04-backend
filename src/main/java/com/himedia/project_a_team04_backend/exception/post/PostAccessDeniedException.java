package com.himedia.project_a_team04_backend.exception.post;

public class PostAccessDeniedException extends RuntimeException{
    public PostAccessDeniedException(){}
    public PostAccessDeniedException(String msg){
        super(msg);
    }
}