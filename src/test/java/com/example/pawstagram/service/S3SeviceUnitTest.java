package com.example.pawstagram.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class S3SeviceUnitTest {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpload() throws IOException {
        String filepath = "uploads/test.jpg";
        String filename = "test.jpg";

        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        doAnswer(invocation -> {
            File fileArg = invocation.getArgument(0);
            fileArg.createNewFile();
            return null;
        }).when(multipartFile).transferTo(any(File.class));

        when(s3Client.putObject(any(PutObjectRequest.class)))
                .thenReturn(new PutObjectResult());

        URL expectedUrl = new URL("https://my-instargam-app.s3.amazonaws.com/" + filepath);
        when(s3Client.getUrl(anyString(), anyString())).thenReturn(expectedUrl);

        String resultUrl = s3Service.upload(multipartFile, filepath);

        assertEquals(expectedUrl.toString(), resultUrl);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture());
        File fileUsed = captor.getValue().getFile();

        assertFalse(fileUsed.exists());
    }

    @Test
    public void testGetFile() {
        String objectKey = "uploads/test.jpg";
        S3Object mockS3Object = new S3Object();

        when(s3Client.getObject(anyString(), eq(objectKey))).thenReturn(mockS3Object);
        S3Object returnedObject = s3Service.getFile(objectKey);

        assertNotNull(returnedObject);
        assertEquals(mockS3Object, returnedObject);
        verify(s3Client).getObject(anyString(), eq(objectKey));
    }
}
