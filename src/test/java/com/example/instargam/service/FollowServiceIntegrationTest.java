package com.example.instargam.service;

import com.example.instargam.model.User;
import com.example.instargam.repository.FollowRepository;
import com.example.instargam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FollowServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FollowService followService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("user1@email");
        user1.setUsername("username_1");
        user1.setPassword("Password111!");
        userRepository.save(user1);

        user2 = new User();
        user2.setEmail("user2@email");
        user2.setUsername("username_2");
        user2.setPassword("Password111!");
        userRepository.save(user2);

        user3 = new User();
        user3.setEmail("user3@email");
        user3.setUsername("username_3");
        user3.setPassword("Password111!");
        userRepository.save(user3);
    }

    @Test
    void successfullyFollowsUser() {
        followService.followUser(user2, "username_1");
        assertEquals(1, followService.getNumberOfFollowing(user2));
        assertEquals(1, followService.getNumberOfFollowers(user1));
        assertTrue(followRepository.existsByFollowerAndFollowing(user2, user1));
    }

    @Test
    void addsOnlyOneFollowIfTryingToFollowFewTimesByTheSameUser() {
        followService.followUser(user2, "username_1");
        followService.followUser(user2, "username_1");
        followService.followUser(user2, "username_1");

        assertEquals(1, followService.getNumberOfFollowing(user2));
        assertEquals(1, followService.getNumberOfFollowers(user1));
        assertTrue(followRepository.existsByFollowerAndFollowing(user2, user1));
    }

    @Test
    void successfullyAddsFollowsFromDifferentUsers() {
        followService.followUser(user2, "username_1");
        followService.followUser(user3, "username_1");

        assertEquals(1, followService.getNumberOfFollowing(user2));
        assertEquals(1, followService.getNumberOfFollowing(user3));
        assertEquals(2, followService.getNumberOfFollowers(user1));
    }

    @Test
    void throwsIllegalArgumentExceptionIfUserTriesToFollowThemself() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()->
                followService.followUser(user1, "username_1"));
        assertEquals("Unable to follow owned account.", e.getMessage());
    }
    @Test
    void throwsEntityNotFoundExceptionIfUserDoesNotExist() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, ()->
                followService.followUser(user1, "fake_username"));
        assertEquals("User doesn't exist!", e.getMessage());
    }

    @Test
    void successfullyUnfollowsUser() {
        followService.followUser(user2, "username_1");
        assertTrue(followRepository.existsByFollowerAndFollowing(user2, user1));

        followService.unfollowUser(user2, "username_1");
        assertFalse(followRepository.existsByFollowerAndFollowing(user2, user1));
    }

    @Test
    void doesNotDoAnythingIfUnfollowsTwice() {
        followService.followUser(user2, "username_1");
        assertTrue(followRepository.existsByFollowerAndFollowing(user2, user1));

        followService.unfollowUser(user2, "username_1");
        assertFalse(followRepository.existsByFollowerAndFollowing(user2, user1));

        followService.unfollowUser(user2, "username_1");
        assertFalse(followRepository.existsByFollowerAndFollowing(user2, user1));
    }

    @Test
    void returnsFollowers() {
        followService.followUser(user2, "username_1");
        followService.followUser(user3, "username_1");

        var followers = followService.getFollowers("username_1");

        assertEquals(2, followers.size());
        assertEquals("username_2", followers.getFirst().getUsername());
    }

    @Test
    void returnsFollowingUsers() {
        followService.followUser(user2, "username_1");
        followService.followUser(user2, "username_3");

        var following = followService.getFollowing("username_2");

        assertEquals(2, following.size());
        assertEquals("username_1", following.getFirst().getUsername());
    }
}