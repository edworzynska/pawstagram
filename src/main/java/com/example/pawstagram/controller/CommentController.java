package com.example.pawstagram.controller;

import com.example.pawstagram.dto.CommentDTO;
import com.example.pawstagram.model.User;
import com.example.pawstagram.service.AuthenticationService;
import com.example.pawstagram.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/posts")
@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthenticationService authenticationService;

    public CommentController(CommentService commentService, AuthenticationService authenticationService) {
        this.commentService = commentService;
        this.authenticationService = authenticationService;
    }
    @PostMapping("/{postId}/add-comment")
    public ResponseEntity<Object> addComment(
            @PathVariable Long postId,
            @RequestBody String contents){

        User loggedUser = authenticationService.getLoggedUser();
        commentService.addComment(postId, loggedUser, contents);

        return new ResponseEntity<>("The comment was added successfully!", HttpStatus.CREATED);
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long postId){
        List<CommentDTO> comments = commentService.commentsToPost(postId);
        if (!comments.isEmpty()){
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }
        else return new ResponseEntity<>("No one commented the post yet!", HttpStatus.OK);
    }
}
