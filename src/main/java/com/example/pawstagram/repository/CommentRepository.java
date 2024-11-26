package com.example.pawstagram.repository;

import com.example.pawstagram.model.Comment;
import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);
    List<Comment> findByPost(Post post);
    Long countByPost(Post post);
    void deleteByUser(User user);
    void deleteByPost(Post post);
}
