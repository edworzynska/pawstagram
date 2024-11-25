package com.example.instargam.controller;


import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.service.AuthenticationService;
import com.example.instargam.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PostController {

    private PostService postService;
    private AuthenticationService authenticationService;
    private PostRepository postRepository;

    @Autowired
    public PostController(PostService postService, PostRepository postRepository, AuthenticationService authenticationService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/add-post")
    public ResponseEntity<Object> addPost(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String description) throws FileUploadException {

        User loggedUser = authenticationService.getLoggedUser();

        postService.createPost(file, description, loggedUser);
        return new ResponseEntity<>("Post uploaded successfully!", HttpStatus.CREATED);
    }
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<Object> deletePost(
            @PathVariable Long postId) {

        User loggedUser = authenticationService.getLoggedUser();

        postService.deletePost(postId, loggedUser);
        return new ResponseEntity<>("Post deleted successfully!", HttpStatus.OK);
    }
}
