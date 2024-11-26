package com.example.pawstagram.repository;

import com.example.pawstagram.model.Follow;
import com.example.pawstagram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);
    List<Follow> findByFollowing(User following);
    List<Follow> findByFollower(User follower);
    Long countByFollowing(User following);
    Long countByFollower(User follower);
}
