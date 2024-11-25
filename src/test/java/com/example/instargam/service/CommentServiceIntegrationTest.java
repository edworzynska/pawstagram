package com.example.instargam.service;

import com.example.instargam.model.Comment;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.CommentRepository;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

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
    void successfullyAddsComment() {
        Long id = post1.getId();
        commentService.addComment(id, user1, "test comment");
        assertEquals(1, commentRepository.findByPost(post1).size());
        assertEquals(1, commentRepository.findByUser(user1).size());
    }

    @Test
    void throwsErrorIfContentsIsEmpty() {
        Long id = post1.getId();
        InvalidParameterException e = assertThrows(InvalidParameterException.class, () ->
                commentService.addComment(id, user1, ""));
        assertEquals("The comment cannot be empty!", e.getMessage());
    }
    @Test
    void throwsErrorIfContentsIsBlank() {
        Long id = post1.getId();
        InvalidParameterException e = assertThrows(InvalidParameterException.class, () ->
                commentService.addComment(id, user1, "      "));
        assertEquals("The comment cannot be empty!", e.getMessage());
    }

    @Test
    void successfullyAddsFewCommentsAsDifferentUsers() {
        Long id = post1.getId();
        commentService.addComment(id, user1, "test comment");
        commentService.addComment(id, user2, "test comment 2");
        commentService.addComment(id, user3, "test comment 3");
        assertEquals(3, commentRepository.findByPost(post1).size());
        assertEquals(1, commentRepository.findByUser(user1).size());
    }

    @Test
    void successfullyAddsFewCommentsAsOneUser() {
        Long id = post1.getId();
        commentService.addComment(id, user1, "test comment");
        commentService.addComment(id, user1, "test comment 2");
        commentService.addComment(id, user1, "test comment 3");
        assertEquals(3, commentRepository.findByPost(post1).size());
        assertEquals(3, commentRepository.findByUser(user1).size());
    }
}
