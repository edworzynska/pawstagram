package com.example.instargam.repository;

import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByUserId(Long userId);
}
