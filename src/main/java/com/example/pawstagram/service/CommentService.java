package com.example.pawstagram.service;

import com.example.pawstagram.dto.CommentDTO;
import com.example.pawstagram.model.Comment;
import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.CommentRepository;
import com.example.pawstagram.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }
    @Transactional
    public void addComment(Long postId, User loggedUser, String contents){

        Post post = postRepository.findById(postId).orElseThrow(()->
                new EntityNotFoundException("Post not found!"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(loggedUser);
        comment.setContents(contents);

        commentRepository.save(comment);
    }
    public List<CommentDTO> commentsToPost(Long postId){

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post not found!"));

        List<Comment> comments = commentRepository.findByPost(post);

        return comments.stream().map(comment ->
                new CommentDTO
                        (comment.getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getProfileImgUrl(),
                        comment.getContents(),
                        comment.getDate()))
                .toList();
    }
}
