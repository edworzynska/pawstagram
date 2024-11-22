package com.example.instargam.controller;

import com.example.instargam.dto.UserDTO;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.service.AuthenticationService;
import com.example.instargam.service.LikeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/posts")
@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public LikeController(LikeService likeService, PostRepository postRepository, AuthenticationService authenticationService) {
        this.likeService = likeService;
        this.postRepository = postRepository;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Object> likePost(
            @PathVariable Long postId){

        User loggedUser = authenticationService.getLoggedUser();
        Post postToLike = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Unable to find the post!"));

        likeService.likePost(postToLike, loggedUser);
        return new ResponseEntity<>("Post liked successfully!", HttpStatus.OK);
    }
    @DeleteMapping("/{postId}/unlike")
    public ResponseEntity<Object> unlikePost(
            @PathVariable Long postId){

        User loggedUser = authenticationService.getLoggedUser();
        Post postToUnlike = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Unable to find the post!"));

        likeService.unlikePost(postToUnlike, loggedUser);
        return new ResponseEntity<>("Post unliked successfully!", HttpStatus.OK);
    }
    @GetMapping("/{postId}/likes")
    public ResponseEntity<Object> getListOfLikes(
            @PathVariable Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Unable to find the post!"));

        List<UserDTO> likes = likeService.postLikes(post);

        if (!likes.isEmpty()){
            return new ResponseEntity<>(likeService.postLikes(post), HttpStatus.FOUND);
        }
        else return new ResponseEntity<>("No one liked the post yet!", HttpStatus.OK);
    }

}
