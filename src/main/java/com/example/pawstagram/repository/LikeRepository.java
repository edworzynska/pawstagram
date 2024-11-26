package com.example.pawstagram.repository;

import com.example.pawstagram.model.Like;
import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPostId(Long postId);
    List<Like> findByPost(Post post);
    Long countByPost(Post post);
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
    void deleteByPost(Post post);
    void deleteByUser(User user);
}
