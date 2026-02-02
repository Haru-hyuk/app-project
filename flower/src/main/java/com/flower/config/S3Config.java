package com.flower.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class S3Config {

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        log.info("S3 Client 초기화 - Region: {}", region);

        return S3Client.builder()
                .region(Region.of(region))
                // DefaultCredentialsProvider는 다음 순서로 자격 증명을 찾음:
                // 1. 환경 변수 (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
                // 2. Java 시스템 속성
                // 3. ~/.aws/credentials 파일
                // 4. EC2 IAM Role (Instance Profile)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
