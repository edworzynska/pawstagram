package com.example.instargam.service;

import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.repository.UserRepository;
import jakarta.annotation.Nullable;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;

@Service
public class PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private S3Service s3Service;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, S3Service s3Service) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public String uploadPic(MultipartFile file, String username) throws FileUploadException {
        String filePath = "/" + username + "/posts/" + file.getOriginalFilename();

        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
            throw new InvalidParameterException("Invalid format of the file. Accepted extensions: .jpeg, .jpg, .png, .jpe");
        }
        try {
            return s3Service.upload(file, filePath);
        } catch (IOException e) {
            throw new FileUploadException("Error uploading the picture!", e);
        }
    }
    @Transactional
    public Post createPost(MultipartFile file, @Nullable String description, User user) throws FileUploadException {

        String fileUrl = uploadPic(file, user.getUsername());

        Post post = new Post();
        post.setImgUrl(fileUrl);
        post.setUser(user);
        if (description != null){
            post.setDescription(description);
        }
        postRepository.save(post);

        return post;
    }
}