package com.example.instargam.service;

import com.example.instargam.dto.PostDTO;
import com.example.instargam.model.Post;
import com.example.instargam.model.User;
import com.example.instargam.repository.*;
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
import java.time.LocalDateTime;
import java.util.List;
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
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private FollowRepository followRepository;

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
        String filepath = "users/doobee/posts/doobee.jpg";
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
        String filepath = "users/doobee/posts/doobee.gif";
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
        String filepath = "users/doobee/posts/doobee.jpg";
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
        String filepath = "users/doobee/posts/doobee.jpg";
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
        assertEquals(user, post.getUser());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void returnsPostDtosByUser() {
        String username = "test_user";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);
        post.setImgUrl("/example_img.jpg");
        post.setDate(LocalDateTime.now());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.findByUser(user)).thenReturn(List.of(post));
        when(commentRepository.countByPost(post)).thenReturn(1L);
        when(likeRepository.countByPost(post)).thenReturn(2L);

        List<PostDTO> result = postService.getPostsByUser(username);

        assertNotNull(result);
        assertEquals(1, result.size());

        PostDTO postDTO = result.get(0);
        assertEquals(post.getId(), postDTO.getId());
        assertEquals(post.getImgUrl(), postDTO.getImgUrl());
        assertEquals(post.getDescription(), postDTO.getDescription());
        assertEquals(1L, postDTO.getCommentsCount());
        assertEquals(2L, postDTO.getLikesCount());

        verify(userRepository).findByUsername(username);
        verify(postRepository).findByUser(user);
        verify(commentRepository).countByPost(post);
        verify(likeRepository).countByPost(post);
    }
}