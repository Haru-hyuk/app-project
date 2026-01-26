package com.flower.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 꽃 이미지 정적 파일 서빙
        // /flower_images/** 요청을 classpath:/flower_images/ 또는 file: 경로로 매핑
        // 실제 파일 위치: src/main/resources/flower_images/
        registry.addResourceHandler("/flower_images/**")
                .addResourceLocations(
                    "classpath:/flower_images/",  // src/main/resources/flower_images/
                    "file:./flower_images/",
                    "file:../flower_images/"
                )
                .setCachePeriod(3600); // 1시간 캐시
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
