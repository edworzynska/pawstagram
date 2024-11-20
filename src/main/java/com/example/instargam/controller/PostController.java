package com.example.instargam.controller;


import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.service.AuthenticationService;
import com.example.instargam.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    public PostController(PostService postService, AuthenticationService authenticationService) {
        this.postService = postService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/add-post")
    public ResponseEntity<Object> addPost(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String description){

        User loggedUser = authenticationService.getLoggedUser();

        try {
            postService.createPost(file, description, loggedUser);
            return new ResponseEntity<>("Post uploaded successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error uploading the post: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
