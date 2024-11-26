package com.example.instargam.controller;

import com.example.instargam.dto.UserDTO;
import com.example.instargam.model.User;
import com.example.instargam.service.AuthenticationService;
import com.example.instargam.service.S3Service;
import com.example.instargam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> signUp
            (@RequestParam String email,
             @RequestParam String username,
             @RequestParam String password){

        userService.createUser(email, username, password);
        return new ResponseEntity<>("User registered successfully! You can sign in.", HttpStatus.CREATED);
    }
    @PostMapping("/upload-profile-pic")
    public ResponseEntity<Object> uploadProfilePic(@RequestParam("file") MultipartFile file) {
        User loggedUser = authenticationService.getLoggedUser();
        if (file.isEmpty()){
            throw new IllegalArgumentException("File cannot be empty!");
        }
        try {
            String fileUrl = userService.uploadProfilePic(file, loggedUser.getUsername());
            return ResponseEntity.ok("Profile picture uploaded successfully. URL: " + fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading profile picture: " + e.getMessage());
        }
    }
    @PostMapping("/add-bio")
    public ResponseEntity<Object> addBio(@RequestParam String bio) {
        User loggedUser = authenticationService.getLoggedUser();
        userService.addBio(bio, loggedUser.getUsername());
        return ResponseEntity.accepted().body("Bio updated successfully!");
    }
    @GetMapping("/{username}")
    public ResponseEntity<Object> getUserInfo(@PathVariable String username){
        UserDTO user = userService.getUserInfo(username);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }
}
