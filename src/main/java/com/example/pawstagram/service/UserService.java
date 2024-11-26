package com.example.pawstagram.service;

import com.example.pawstagram.dto.UserDTO;
import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.LikeRepository;
import com.example.pawstagram.repository.PostRepository;
import com.example.pawstagram.repository.UserRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private S3Service s3Service;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PostRepository postRepository, LikeRepository likeRepository, S3Service s3Service, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.s3Service = s3Service;
        this.passwordEncoder = passwordEncoder;
    }

    private Boolean validatePassword (String password) {
        boolean areRequirementsMet = false;
        if (password.length() >= 8) {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            if (hasLetter.find() && hasDigit.find() && hasSpecial.find()) {
                areRequirementsMet = true;
            }
        }
        return areRequirementsMet;
    }

    private Boolean validateEmailAddress(String email) {
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    private Boolean validateUsername (String username) {
        String regex = "^[a-zA-Z0-9_]{3,15}$";
        return username != null && username.matches(regex);
    }

    @Transactional
    public User createUser(String email, String username, String password){
        if (!validateEmailAddress(email)){
            throw new InvalidParameterException("Provided email address is invalid!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new EntityExistsException("An account with this email address already exists!");
        }
        if (userRepository.existsByUsername(username)) {
            throw new EntityExistsException("This username is already taken!");
        }
        if (!validateUsername(username)){
            throw new InvalidParameterException("Username must be 3-15 characters long and contain only letters, numbers and underscores.");
        }
        if (!validatePassword(password)) {
            throw new SecurityException("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!");
        }
        User user = new User();
        user.setEmail(email);
        user.setUsername(username.toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return user;
    }

    @Transactional
    public String uploadProfilePic(MultipartFile file, String username) throws FileUploadException {
        String filePath = "users/" + username + "/profile-pics/" + file.getOriginalFilename();

        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
            throw new InvalidParameterException("Invalid format of the file. Accepted extensions: .jpeg, .jpg, .png, .jpe");
        }

        try {
            String fileUrl = s3Service.upload(file, filePath);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.setProfileImgUrl(fileUrl);
            userRepository.save(user);

            return fileUrl;

        } catch (IOException e) {
            throw new FileUploadException("Error uploading profile picture", e);
        }
    }
    @Transactional
    public void addBio(String bio, String username){
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("User not found"));
        user.setBio(bio);
        userRepository.save(user);
    }
    @Transactional
    public void deleteUser(User user){
        likeRepository.deleteByUser(user);
        postRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    public UserDTO getUserInfo(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User not found!"));

        return new UserDTO
                (user.getId(), user.getUsername(), user.getProfileImgUrl(),
                        "/users/" + user.getUsername(), user.getBio());
    }
}