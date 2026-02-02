package com.flower.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:florit-images}")
    private String bucketName;

    @Value("${aws.s3.base-url:https://florit-images.s3.us-east-1.amazonaws.com}")
    private String baseUrl;

    /**
     * 프로필 이미지 업로드
     * @param file 업로드할 파일
     * @param userId 사용자 ID
     * @return S3 URL
     */
    public String uploadProfileImage(MultipartFile file, Integer userId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        // 파일명: profile_images/{userId}_{uuid}.{ext}
        String key = String.format("profile_images/%d_%s.%s",
                userId, UUID.randomUUID().toString().substring(0, 8), extension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        String s3Url = baseUrl + "/" + key;
        log.info("프로필 이미지 업로드 완료: {}", s3Url);

        return s3Url;
    }

    /**
     * S3 파일 삭제
     * @param fileUrl S3 URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || !fileUrl.contains(bucketName)) {
            return;
        }

        try {
            // URL에서 key 추출
            String key = fileUrl.replace(baseUrl + "/", "");

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("S3 파일 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
