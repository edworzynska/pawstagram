package com.example.pawstagram.service;

import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.LikeRepository;
import com.example.pawstagram.repository.PostRepository;
import com.example.pawstagram.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LikeServiceIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    private User user1;
    private User user2;
    private User user3;
    private Post post1;

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

        post1 = new Post();
        post1.setImgUrl("users/test/profile-pics/IMG_1617.jpeg");
        post1.setUser(user1);
        post1.setDescription("test test");
        postRepository.save(post1);
    }

    @Test
    void successfullyLikesPost() {
        likeService.likePost(post1, user1);
        assertEquals(1, likeRepository.countByPost(post1));
    }

    @Test
    void doesntChangeLikeCountIfLikedFewTimesByTheSameUser() {
        likeService.likePost(post1, user1);
        assertEquals(1, likeRepository.countByPost(post1));

        likeService.likePost(post1, user1);
        likeService.likePost(post1, user1);
        likeService.likePost(post1, user1);
        assertEquals(1, likeRepository.countByPost(post1));
    }

    @Test
    void fewUsersLikePost() {
        likeService.likePost(post1, user1);
        likeService.likePost(post1, user2);
        likeService.likePost(post1, user3);
        assertEquals(3, likeRepository.countByPost(post1));
    }

    @Test
    void unlikesPostSuccessfully() {
        likeService.likePost(post1, user1);
        likeService.likePost(post1, user2);
        likeService.likePost(post1, user3);
        assertEquals(3, likeRepository.countByPost(post1));

        likeService.unlikePost(post1, user3);
        assertEquals(2, likeRepository.countByPost(post1));
    }

    @Test
    void doesntChangeLikeCountIfOneUserUnlikesFewTimes() {
        likeService.likePost(post1, user1);
        likeService.likePost(post1, user2);
        likeService.likePost(post1, user3);
        assertEquals(3, likeRepository.countByPost(post1));

        likeService.unlikePost(post1, user3);
        assertEquals(2, likeRepository.countByPost(post1));

        likeService.unlikePost(post1, user3);
        likeService.unlikePost(post1, user3);
        likeService.unlikePost(post1, user3);
        assertEquals(2, likeRepository.countByPost(post1));
    }

    @Test
    void getsNumberOfLikes() {
        likeService.likePost(post1, user1);
        likeService.likePost(post1, user2);
        likeService.likePost(post1, user3);
        assertEquals(3, likeRepository.countByPost(post1));
        assertEquals(3, likeService.getNumberOfLikes(post1));
    }

    @Test
    void returnsListOfUserDtosWhoLikedPost() {
        likeService.likePost(post1, user1);
        likeService.likePost(post1, user2);
        likeService.likePost(post1, user3);
        assertEquals(3, likeRepository.countByPost(post1));

        assertEquals(3, likeService.getLikesByPost(post1).size());
        assertEquals("username_1", likeService.getLikesByPost(post1).getFirst().getUsername());
        assertEquals("/users/username_1", likeService.getLikesByPost(post1).getFirst().getProfileUrl());
    }

    @Test
    void likeIsDeletedIfPostIsDeleted() {
        likeService.likePost(post1, user1);
        assertEquals(1, likeRepository.count());
        postService.deletePost(post1.getId(), user1);
        assertEquals(0, likeRepository.count());
    }
}