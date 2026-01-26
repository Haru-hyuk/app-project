package com.flower.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 🔓 JWT 검사 제외 (토큰 발급/복구용 API + 정적 리소스)
        return uri.equals("/api/auth/login")
                || uri.equals("/api/auth/signup")
                || uri.equals("/api/auth/refresh")
                || uri.equals("/api/auth/check-email")
                || uri.equals("/api/auth/check-nickname")
                || uri.equals("/api/auth/find-email")
                || uri.equals("/api/auth/reset-password")
                || uri.startsWith("/oauth2/")
                || uri.startsWith("/login/oauth2/")
                || uri.startsWith("/flower_images/")  // 정적 이미지 리소스 제외
                || uri.startsWith("/api/keywords/popular")  // 인기 키워드 제외
                || uri.startsWith("/api/keywords/trending");  // 트렌딩 키워드 제외
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null) {
            try {
                // JWT 유효성 검증
                jwtTokenProvider.validateTokenOrThrow(token);

                String email = jwtTokenProvider.getEmailFromToken(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_USER")
                                )
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                request.setAttribute("jwtException", e);
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute("jwtException", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
