package com.example.pawstagram.controller;

import com.example.pawstagram.model.Post;
import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.PostRepository;
import com.example.pawstagram.repository.UserRepository;
import com.example.pawstagram.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

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
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void addsCommentAsLoggedUser() throws Exception{
        Long postId = post1.getId();
        mockMvc.perform(post("/posts/{postId}/add-comment", postId)
                .param("contents", "test comment"))
                .andExpect(status().isCreated())
                .andExpect(content().string("The comment was added successfully!"));
    }

    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void throwsErrorIfContentsAreEmpty() throws Exception{
        Long postId = post1.getId();
        mockMvc.perform(post("/posts/{postId}/add-comment", postId)
                        .param("contents", ""))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("The comment cannot be empty!"));
    }
    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void throwsErrorIfContentsAreBlank() throws Exception{
        Long postId = post1.getId();
        mockMvc.perform(post("/posts/{postId}/add-comment", postId)
                        .param("contents", "    "))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("The comment cannot be empty!"));
    }
    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void throwsMissingParameterErrorIfNoParam() throws Exception{
        Long postId = post1.getId();
        mockMvc.perform(post("/posts/{postId}/add-comment", postId))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Missing parameter: contents"));
    }
}