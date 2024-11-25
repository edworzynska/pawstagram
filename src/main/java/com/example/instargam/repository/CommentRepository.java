package com.example.instargam.repository;

import com.example.instargam.model.Comment;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUser(User user);
    List<Comment> findByPost(Post post);
    void deleteByUser(User user);
    void deleteByPost(Post post);
}
