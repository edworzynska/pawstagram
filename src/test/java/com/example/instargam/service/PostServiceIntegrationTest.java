package com.example.instargam.service;

import com.example.instargam.dto.PostDTO;
import com.example.instargam.model.Follow;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.FollowRepository;
import com.example.instargam.repository.PostRepository;
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
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FollowRepository followRepository;

    private User testUser;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("test_user");
        testUser.setEmail("email@email");
        testUser.setPassword("password22!");
        userRepository.save(testUser);

        post1 = new Post();
        post1.setUser(testUser);
        post1.setImgUrl("/url1.jpg");
        post1.setDescription("test 1");
        postRepository.save(post1);

        post2 = new Post();
        post2.setUser(testUser);
        post2.setImgUrl("/url2.jpg");
        post2.setDescription("test 2");
        postRepository.save(post2);
    }

    @Test
    void getPostsByUser() {

        List<PostDTO> posts = postService.getPostsByUser("test_user");

        assertEquals(2, posts.size());
        assertEquals("test 1", posts.get(0).getDescription());
        assertEquals("test 2", posts.get(1).getDescription());
    }

    @Test
    void throwsEntityNotFoundExceptionIfUnableToFindTheUser() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                postService.getPostsByUser("fake_user"));
        assertEquals("User not found!", e.getMessage());
    }

    @Test
    void getPostsByUserReturnsEmptyListForUserWithoutPosts() {
        postRepository.deleteByUser(testUser);
        List<PostDTO> posts = postService.getPostsByUser("test_user");

        assertNotNull(posts);
        assertTrue(posts.isEmpty());
    }

    @Test
    void returnsFollowedUsersPostsAsDtos() {
        User followedUser = new User();
        followedUser.setEmail("email2@email");
        followedUser.setUsername("username2");
        followedUser.setPassword("password111!");
        userRepository.save(followedUser);

        Post post = new Post();
        post.setUser(followedUser);
        post.setImgUrl("/url.jpg");
        post.setDescription("followed user's post");
        postRepository.save(post);

        Follow follow = new Follow(testUser, followedUser);
        followRepository.save(follow);

        List<PostDTO> posts = postService.followedUsersPosts("test_user");

        assertEquals(1, posts.size());
        assertEquals("followed user's post", posts.getFirst().getDescription());
        assertEquals("username2", posts.getFirst().getUsername());
    }
    @Test
    void followedUsersPostsReturnsEmptyListWhenUserFollowsNoOne() {
        List<PostDTO> posts = postService.followedUsersPosts("test_user");

        assertNotNull(posts);
        assertTrue(posts.isEmpty());
    }

    @Test
    void followedUsersPostsThrowsExceptionForInvalidUsername() {
        Exception e = assertThrows(EntityNotFoundException.class, () ->
                postService.followedUsersPosts("fake_user"));

        assertEquals("User not found!", e.getMessage());
    }
    @Test
    void deletesPost() {

        Post post = new Post();
        post.setUser(testUser);
        post.setImgUrl("/url.jpeg");
        post.setDescription("Post to delete");
        postRepository.save(post);

        postService.deletePost(post.getId(), testUser);

        assertFalse(postRepository.existsById(post.getId()));
    }
    @Test
    void deletePostThrowsExceptionForInvalidPostId() {
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                postService.deletePost(999L, testUser));

        assertEquals("Post not found!", exception.getMessage());
    }
    @Test
    void deletePostThrowsExceptionForUnauthorizedUser() {
        User unauthorizedUser = new User();
        unauthorizedUser.setUsername("unauthorizedUser");
        unauthorizedUser.setPassword("password11!");
        unauthorizedUser.setEmail("unauthorized@email");
        userRepository.save(unauthorizedUser);

        Post post = new Post();
        post.setUser(testUser);
        post.setImgUrl("/url.jpg");
        post.setDescription("Unauthorized deletion test");
        postRepository.save(post);

        Exception e = assertThrows(SecurityException.class, () ->
                postService.deletePost(post.getId(), unauthorizedUser));

        assertEquals("Access denied!", e.getMessage());
    }
}