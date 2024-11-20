package com.example.instargam.service;

import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.PostRepository;
import com.example.instargam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void successfullyUploadsPicAndReturnsUrl() throws IOException {
        String username = "doobee";
        String filepath = "/doobee/posts/doobee.jpg";
        String s3Url = "https://s3.amazonaws.com/my-instargam-app/" + filepath;

        User user = new User();
        user.setUsername(username);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "doobee.jpg",
                "image/jpeg",
                "test image content".getBytes());

        when(s3Service.upload(any(MultipartFile.class), eq(filepath))).thenReturn(s3Url);
        String result = postService.uploadPic(mockFile, username);

        verify(s3Service, times(1)).upload(any(),any());
        assertEquals(s3Url, result);
    }
    @Test
    void throwsInvalidParameterExceptionIfInvalidFileFormat() throws IOException {
        String username = "doobee";
        String filepath = "/doobee/posts/doobee.gif";
        String s3Url = "https://s3.amazonaws.com/my-instargam-app/" + filepath;

        User user = new User();
        user.setUsername(username);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "doobee.gif",
                "image/gif",
                "test image content".getBytes());

        when(s3Service.upload(any(MultipartFile.class), eq(filepath))).thenReturn(s3Url);
        InvalidParameterException e = assertThrows(InvalidParameterException.class, () ->
                postService.uploadPic(mockFile, username));

        verify(s3Service, never()).upload(any(),any());
        assertEquals("Invalid format of the file. Accepted extensions: .jpeg, .jpg, .png, .jpe", e.getMessage());
    }

    @Test
    void createsPostSuccessfully() throws IOException {
        String username = "doobee";
        String filepath = "/doobee/posts/doobee.jpg";
        String s3Url = "https://s3.amazonaws.com/my-instargam-app/" + filepath;

        User user = new User();
        user.setUsername(username);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "doobee.jpg",
                "image/jpeg",
                "test image content".getBytes());

        when(s3Service.upload(any(MultipartFile.class), eq(filepath))).thenReturn(s3Url);

        Post post = postService.createPost(mockFile, "test", user);
        assertNotNull(post);
        assertEquals("test", post.getDescription());
        assertEquals(s3Url, post.getImgUrl());
        verify(postRepository, times(1)).save(post);
    }
    @Test
    void createsPostWithNullDescriptionSuccessfully() throws IOException {
        String username = "doobee";
        String filepath = "/doobee/posts/doobee.jpg";
        String s3Url = "https://s3.amazonaws.com/my-instargam-app/" + filepath;

        User user = new User();
        user.setUsername(username);

        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "doobee.jpg",
                "image/jpeg",
                "test image content".getBytes());

        when(s3Service.upload(any(MultipartFile.class), eq(filepath))).thenReturn(s3Url);

        Post post = postService.createPost(mockFile, null, user);
        assertNotNull(post);
        assertNull(post.getDescription());
        assertEquals(s3Url, post.getImgUrl());
        verify(postRepository, times(1)).save(post);
    }
}