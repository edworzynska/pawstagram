package com.example.pawstagram.controller;

import com.example.pawstagram.dto.UserDTO;
import com.example.pawstagram.model.User;
import com.example.pawstagram.service.AuthenticationService;
import com.example.pawstagram.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/api")
@RestController
public class FollowController {

    private FollowService followService;

    private AuthenticationService authenticationService;

    @Autowired
    public FollowController(FollowService followService, AuthenticationService authenticationService) {
        this.followService = followService;
        this.authenticationService = authenticationService;
    }
    @PostMapping("/{username}/follow")
    public ResponseEntity<Object> follow(
            @PathVariable String username){

        User loggedUser = authenticationService.getLoggedUser();
        followService.followUser(loggedUser, username);

        return new ResponseEntity<>("Successfully followed the user!", HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/{username}/unfollow")
    public ResponseEntity<Object> unfollow(
            @PathVariable String username){

        User loggedUser = authenticationService.getLoggedUser();
        followService.unfollowUser(loggedUser, username);

        return new ResponseEntity<>("Successfully unfollowed the user!", HttpStatus.ACCEPTED);
    }
    @GetMapping("/{username}/followers")
    public ResponseEntity<Object> getFollowers(
            @PathVariable String username){

        List<UserDTO> followers = followService.getFollowers(username);
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }
    @GetMapping("/{username}/following")
    public ResponseEntity<Object> getFollowing(
            @PathVariable String username){

        List<UserDTO> following = followService.getFollowing(username);
        return new ResponseEntity<>(following, HttpStatus.OK);
    }
}
