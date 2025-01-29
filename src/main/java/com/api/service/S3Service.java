package com.api.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    
    // 가게 이미지 업로드
    @Transactional
    public String uploadStoreImage(MultipartFile file, Long storeId) throws IOException {
        String fileName = String.format("store/%d/%s_%s", 
            storeId, 
            UUID.randomUUID().toString(), 
            file.getOriginalFilename()
        );
        return uploadFile(file, fileName);
    }
    
    // 메뉴 이미지 업로드
    @Transactional
    public String uploadMenuImage(MultipartFile file, Long storeId) throws IOException {
        String fileName = String.format("menu/%d/%s_%s", 
            storeId, 
            UUID.randomUUID().toString(), 
            file.getOriginalFilename()
        );
        return uploadFile(file, fileName);
    }
    
    // 실제 파일 업로드 처리
    private String uploadFile(MultipartFile file, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}