package com.example.pawstagram.controller;

import com.example.pawstagram.model.User;
import com.example.pawstagram.repository.UserRepository;
import com.example.pawstagram.service.FollowService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class FollowControllerTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


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
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void successfullyFollowsUser() throws Exception {
        mockMvc.perform(post("/{username}/follow", user2.getUsername()))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Successfully followed the user!"));
    }
    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void returnsErrorStatusIfTryingToFollowThemself() throws Exception {
        mockMvc.perform(post("/{username}/follow", user1.getUsername()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Unable to follow owned account."));
    }

    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void successfullyUnfollowsUser() throws Exception {
        mockMvc.perform(delete("/{username}/unfollow", user2.getUsername()))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Successfully unfollowed the user!"));
    }

    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void returnsListOfFollowers() throws Exception {
        followService.followUser(user2, "username_1");
        mockMvc.perform(get("/{username}/followers", user1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
    @Test
    @WithUserDetails(value = "user1@email", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void returnsListOfFollowingUsers() throws Exception {
        followService.followUser(user1, "username_2");
        mockMvc.perform(get("/{username}/followers", user1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}