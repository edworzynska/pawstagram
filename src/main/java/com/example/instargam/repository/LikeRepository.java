package com.example.instargam.repository;

import com.example.instargam.model.Like;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
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
