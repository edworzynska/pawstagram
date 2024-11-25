package com.example.instargam.service;

import com.example.instargam.model.Comment;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.CommentRepository;
import com.example.instargam.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
