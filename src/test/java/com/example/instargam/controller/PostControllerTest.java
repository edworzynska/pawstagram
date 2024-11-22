package com.example.instargam.controller;

import com.example.instargam.model.User;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.repository.UserRepository;
import com.example.instargam.service.AuthenticationService;
import com.example.instargam.service.S3Service;
import com.example.instargam.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = new User();
        testUser.setUsername("test_user123");
        testUser.setEmail("test.email@email.com");
        testUser.setPassword(passwordEncoder.encode("testpassword1!"));
        userRepository.save(testUser);
    }

    @Test
    @WithUserDetails(value = "test.email@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void uploadsPost() throws Exception{

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "sample image content".getBytes()
        );
        mockMvc.perform(multipart("/add-post")
                .file(mockFile)
                .with(user(testUser.getEmail()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("description", "test text"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Post uploaded successfully!"));
    }
    @Test
    @WithUserDetails(value = "test.email@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void uploadsPostWithEmptyDescription() throws Exception{

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "sample image content".getBytes()
        );
        mockMvc.perform(multipart("/add-post")
                        .file(mockFile)
                        .with(user(testUser.getEmail()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().string("Post uploaded successfully!"));
    }

}
