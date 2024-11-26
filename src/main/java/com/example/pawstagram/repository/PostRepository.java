package com.example.pawstagram.repository;

import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByUserId(Long userId);
    void deleteByUser(User user);
    List<Post> findByUserInOrderByDateDesc(List<User> users);
}
