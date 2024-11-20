package com.example.instargam.service;

import com.example.instargam.configuration.S3Config;
import com.example.instargam.model.User;
import com.example.instargam.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3Config s3Config;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createsUserIfDoesntExist() {
        String email = "test@example.com";
        String name = "testuser";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        User test = userService.createUser(email, name, password);

        assertNotNull(test);
        assertEquals(email, test.getEmail());
        assertEquals(name, test.getUsername());
        assertEquals(encodedPassword, test.getPassword());

        verify(userRepository, times(1)).save(test);
    }

    @Test
    void throwsExceptionIfEmailAlreadyInUse() {
        String email = "test@example.com";
        String name = "Test User";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        EntityExistsException e = assertThrows(EntityExistsException.class, ()->userService.createUser(email, name, password));
        assertEquals("An account with this email address already exists!", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void throwsExceptionIfUsernameAlreadyInUse() {
        String email = "test@example.com";
        String username = "test_user";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";

        when(userRepository.existsByUsername(username)).thenReturn(true);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        EntityExistsException e = assertThrows(EntityExistsException.class, ()->userService.createUser(email, username, password));
        assertEquals("This username is already taken!", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void throwsExceptionIfPasswordIsInvalid() {
        String email = "test@example.com";
        String name = "test_user";
        String password = "pass";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        SecurityException e = assertThrows(SecurityException.class, ()->userService.createUser(email, name, password));
        assertEquals("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void throwsExceptionIfUsernameIsTooShort() {
        String email = "test@example.com";
        String name = "t";
        String password = "password!23";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(name)).thenReturn(false);
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser(email, name, password));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void throwsExceptionIfUsernameIsTooLong() {
        String email = "test@example.com";
        String name = "testtesttesttesttesttesttesttesttest";
        String password = "password!23";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(name)).thenReturn(false);
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser(email, name, password));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void throwsExceptionIfUsernameContainsForbiddenCharacters() {
        String email = "test@example.com";
        String name = "test.user";
        String password = "password!23";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(name)).thenReturn(false);
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser(email, name, password));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void throwsExceptionIfUsernameContainsForbiddenCharacters2() {
        String email = "test@example.com";
        String name = "test!user";
        String password = "password!23";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByUsername(name)).thenReturn(false);
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser(email, name, password));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void successfullyUploadsProfilePicture() throws IOException {
        String username = "doobee";
        String filepath = "users/doobee/profile-pics/doobee.jpg";
        String s3Url = "https://s3.amazonaws.com/my-instargam-app/" + filepath;

        User user = new User();
        user.setUsername(username);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "doobee.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(s3Service.upload(any(MultipartFile.class), eq(filepath))).thenReturn(s3Url);

        String result = userService.uploadProfilePic(mockFile, username);

        verify(userRepository, times(1)).save(user);
        assertEquals(s3Url, user.getProfileImgUrl());
        assertEquals(s3Url, result);
    }
    @Test
    void testUploadProfilePicInvalidFileType() {
        User user = new User();
        user.setUsername("doo");

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("image/gif");

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
            userService.uploadProfilePic(mockFile, "doo");
        });

        assertEquals("Invalid format of the file. Accepted extensions: .jpeg, .jpg, .png, .jpe", exception.getMessage());
    }

    @Test
    void addsBioToTheAccount() {
        String email = "test@example.com";
        String name = "testuser";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        User test = userService.createUser(email, name, password);

        when(userRepository.findByUsername(name)).thenReturn(Optional.of(test));

        userService.addBio("some text", "testuser");

        assertEquals("some text", test.getBio());
        verify(userRepository, times(2)).save(test);
    }
    @Test
    void throwsInvalidParameterExceptionIfBioIsBlank() {
        String email = "test@example.com";
        String name = "testuser";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        User test = userService.createUser(email, name, password);

        when(userRepository.findByUsername(name)).thenReturn(Optional.of(test));

        InvalidParameterException e  = assertThrows(InvalidParameterException.class, () ->
                userService.addBio("   ", "testuser"));
        assertEquals("Bio cannot be empty!", e.getMessage());
        assertNull(test.getBio());
        verify(userRepository, times(1)).save(test);
    }
    @Test
    void throwsInvalidParameterExceptionIfBioIsEmpty() {
        String email = "test@example.com";
        String name = "testuser";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        User test = userService.createUser(email, name, password);

        when(userRepository.findByUsername(name)).thenReturn(Optional.of(test));

        InvalidParameterException e  = assertThrows(InvalidParameterException.class, () ->
                userService.addBio("", "testuser"));
        assertEquals("Bio cannot be empty!", e.getMessage());
        assertNull(test.getBio());
        verify(userRepository, times(1)).save(test);
    }
    @Test
    void throwsInvalidParameterExceptionIfBioIsOver150Characters() {
        String email = "test@example.com";
        String name = "testuser";
        String password = "!password7";
        String encodedPassword = "Encoded!password7";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        User test = userService.createUser(email, name, password);

        when(userRepository.findByUsername(name)).thenReturn(Optional.of(test));

        InvalidParameterException e  = assertThrows(InvalidParameterException.class, () ->
                userService.addBio("doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee " +
                        "doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee " +
                        "doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee ", "testuser"));
        assertEquals("Invalid length! Maximum number of characters: 150", e.getMessage());
        assertNull(test.getBio());
        verify(userRepository, times(1)).save(test);
    }
}