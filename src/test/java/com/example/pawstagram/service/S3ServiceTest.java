package com.example.pawstagram.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class S3ServiceTest {

    @Autowired
    private S3Service s3Service;

    @Test
    public void testUploadFile() throws IOException {
        File file = new File("src/test/resources/IMG_1617.jpeg");
        String filepath = "users/test/profile-pics/IMG_1617.jpeg";

        String fileUrl = s3Service.upload(new MockMultipartFile("file", file.getName(), "image/jpeg", new FileInputStream(file)), filepath);

        assertNotNull(fileUrl);
        System.out.println("Uploaded file URL: " + fileUrl);
    }
}