package com.himedia.project_a_team04_backend.controller.post;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    @GetMapping("test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("PostController is working!");
    }

}


