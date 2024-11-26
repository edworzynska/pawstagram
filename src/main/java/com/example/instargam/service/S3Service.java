package com.example.instargam.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class S3Service {
    private static final String BUCKET_NAME = "my-instargam-app";
    private AmazonS3 s3Client;

    @Autowired
    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file, String filepath) throws IOException {
        File tempFile = convertMultipartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(BUCKET_NAME, filepath, tempFile));
        tempFile.delete();
        return s3Client.getUrl(BUCKET_NAME, filepath).toString();

    }

    public S3Object getFile(String objectKey){
        return s3Client.getObject(BUCKET_NAME, objectKey);
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }
}
