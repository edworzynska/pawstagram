package com.example.instargam.controller;

import com.example.instargam.model.User;
import com.example.instargam.repository.UserRepository;
import com.example.instargam.service.AuthenticationService;
import com.example.instargam.service.S3Service;
import com.example.instargam.service.UserService;
import org.hamcrest.Matchers;
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
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserService userService;

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
    void createsUser() throws Exception {
        mockMvc.perform(post("/register").param("email", "test@email.com")
                .param("username", "test_name")
                .param("password", "Password1!"))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully! You can sign in."));
    }

    @Test
    void throwsMissingParametersExceptionIfParamsAreMissing() throws Exception{
        mockMvc.perform(post("/register").param("email", "test@email.com")
                        .param("username", "test_name"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Missing parameter: password"));
    }

    @Test
    void throwsInvalidParameterExceptionIfNameIsEmpty() throws Exception {
        mockMvc.perform(post("/register").param("email", "test@email.com")
                        .param("username", "")
                        .param("password", "Password!1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Username must be 3-15 characters long and contain only letters, numbers and underscores."));
    }
    @Test
    void throwsInvalidParameterExceptionIfEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/register").param("email", "test@")
                        .param("username", "test_name")
                        .param("password", "Password!1"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Provided email address is invalid!"));
    }
    @Test
    void throwsSecurityExceptionIfPasswordIsInvalid() throws Exception {
        mockMvc.perform(post("/register").param("email", "test@test.com")
                        .param("username", "test_name")
                        .param("password", "Pass"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!"));
    }

    @Test
    void throwsEntityExistExceptionIfEmailIsInUse() throws Exception{
        mockMvc.perform(post("/register").param("email", "test@test.com")
                        .param("username", "test_name")
                        .param("password", "Password1!"));

        mockMvc.perform(post("/register").param("email", "test@test.com")
                        .param("username", "test_name2")
                        .param("password", "Passworrr12!"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("An account with this email address already exists!"));
    }

    @Test
    @WithUserDetails(value = "test.email@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void uploadsProfilePic() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "sample image content".getBytes()
        );

        mockMvc.perform(multipart("/upload-profile-pic")
                        .file(mockFile)
                        .with(user(testUser.getEmail()))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Profile picture uploaded successfully")));
    }
    @Test
    @WithUserDetails(value = "test.email@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void addsBio() throws Exception {
        mockMvc.perform(post("/add-bio")
                .param("bio", "some bio"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Bio updated successfully!"));
    }
    @Test
    @WithUserDetails(value = "test.email@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void throwsErrorIfBioDoesntMeetRequirements() throws Exception {
        mockMvc.perform(post("/add-bio")
                        .param("bio", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bio cannot be empty!"));
    }
    @Test
    @WithUserDetails(value = "test.email@email.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void throwsErrorIfMissingParameter() throws Exception {
        mockMvc.perform(post("/add-bio"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Missing parameter: bio"));
    }

}