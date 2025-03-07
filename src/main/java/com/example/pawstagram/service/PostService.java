package com.example.pawstagram.service;

import com.example.pawstagram.dto.PostDTO;
import com.example.pawstagram.model.Follow;
import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

@Service
public class PostService {

    private PostRepository postRepository;
    private LikeRepository likeRepository;
    private CommentRepository commentRepository;
    private FollowRepository followRepository;
    private UserRepository userRepository;
    private S3Service s3Service;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, FollowRepository followRepository, S3Service s3Service) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public String uploadPic(MultipartFile file, String username) throws FileUploadException {
        String filePath = "users/" + username + "/posts/" + file.getOriginalFilename();

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
    public PostDTO getPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post not found!"));

        return new PostDTO(post.getId(),
                post.getUser().getUsername(),
                post.getImgUrl(),
                post.getDescription(),
                commentRepository.countByPost(post),
                likeRepository.countByPost(post),
                post.getDate());
    }
    @Transactional
    public void deletePost(Long postId, User loggedUser){

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post not found!"));

        if( post.getUser().equals(loggedUser)){
            likeRepository.deleteByPost(post);
            postRepository.delete(post);
        }
        else throw new SecurityException("Access denied!");
    }

    public Page<PostDTO> getAllPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "date"));
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> new PostDTO(
                post.getId(),
                post.getUser().getUsername(),
                post.getImgUrl(),
                post.getDescription(),
                commentRepository.countByPost(post),
                likeRepository.countByPost(post),
                post.getDate()));
    }
    public List<PostDTO> getPostsByUser(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User not found!"));
        List<Post> posts = postRepository.findByUser(user);

        return posts.stream().map(post -> new PostDTO(
                post.getId(),
                username,
                post.getImgUrl(),
                post.getDescription(),
                commentRepository.countByPost(post),
                likeRepository.countByPost(post),
                post.getDate())).toList();
    }
    public List<PostDTO> followedUsersPosts(User follower){

        List<User> followedUsers = getFollowedUsers(follower);
        List<Post> followedUsersPosts = postRepository.findByUserInOrderByDateDesc(followedUsers);

        return followedUsersPosts.stream().map(post -> new PostDTO(
                post.getId(),
                post.getUser().getUsername(),
                post.getImgUrl(),
                post.getDescription(),
                commentRepository.countByPost(post),
                likeRepository.countByPost(post),
                post.getDate())).toList();
    }

    private List<User> getFollowedUsers(User follower){
        List<Follow> follows = followRepository.findByFollower(follower);
        return follows.stream().map(Follow::getFollowing).toList();
    }

}
