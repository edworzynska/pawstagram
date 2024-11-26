package com.example.pawstagram.controller;

import com.example.pawstagram.dto.PostDTO;
import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.PostRepository;
import com.example.pawstagram.service.AuthenticationService;
import com.example.pawstagram.service.PostService;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RequestMapping("/api")
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
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Object> getPost(@PathVariable Long postId){
        PostDTO post = postService.getPost(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<Object> deletePost(
            @PathVariable Long postId) {

        User loggedUser = authenticationService.getLoggedUser();

        postService.deletePost(postId, loggedUser);
        return new ResponseEntity<>("Post deleted successfully!", HttpStatus.OK);
    }
    @GetMapping("/{username}/posts")
    public ResponseEntity<Object> getPostsByUser(@PathVariable String username){
        List<PostDTO> postsByUser = postService.getPostsByUser(username);

        return new ResponseEntity<>(postsByUser, HttpStatus.ACCEPTED);
    }
    @GetMapping("/feed")
    public ResponseEntity<Object> getPostsByFollowing(){
        User loggedUser = authenticationService.getLoggedUser();
        List<PostDTO> posts = postService.followedUsersPosts(loggedUser);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
    @GetMapping("/posts")
    public ResponseEntity<Object> getAllPosts(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<PostDTO> posts = postService.getAllPosts(pageNo, pageSize);

        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
