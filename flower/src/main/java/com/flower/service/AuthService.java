package com.flower.service;

import com.flower.config.jwt.JwtTokenProvider;
import com.flower.dto.auth.*;
import com.flower.entity.RefreshToken;
import com.flower.entity.User;
import com.flower.repository.RefreshTokenRepository;
import com.flower.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 14;

    /* =========================
       회원가입
       ========================= */
    @Transactional
    public void signup(SignupRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByNickname(req.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nickname(req.getNickname())
                .userName(req.getUserName())
                .userBirth(req.getUserBirth())
                .build();

        userRepository.save(user);
    }

    /* =========================
       로그인 + JWT 발급
       ========================= */
    @Transactional
    public TokenResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.")
                );

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        LocalDateTime expiredAt =
                LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS);

        // 동일 유저 재로그인 시 Refresh Token 갱신
        RefreshToken tokenEntity = refreshTokenRepository
                .findByUserEmail(user.getEmail())
                .orElse(
                        RefreshToken.builder()
                                .userEmail(user.getEmail())
                                .token(refreshToken)
                                .expiredAt(expiredAt)
                                .build()
                );

        tokenEntity.updateToken(refreshToken, expiredAt);
        refreshTokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    /* =========================
       Refresh 토큰 재발급
       ========================= */
    @Transactional
    public TokenResponse refresh(String refreshToken) {

        // 1. Refresh Token JWT 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("리프레시 토큰이 만료되었거나 유효하지 않습니다.");
        }

        // 2. DB에 저장된 토큰인지 확인
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new RuntimeException("리프레시 토큰이 유효하지 않습니다.")
                );

        // 3. DB 기준 만료 체크
        if (saved.isExpired()) {
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }

        // 4. 사용자 존재 여부 확인
        User user = userRepository.findByEmail(saved.getUserEmail())
                .orElseThrow(() ->
                        new RuntimeException("존재하지 않는 사용자입니다.")
                );

        // 5. 토큰 회전
        String newAccess = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefresh = jwtTokenProvider.generateRefreshToken(user.getEmail());

        LocalDateTime newExpiredAt =
                LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS);

        saved.updateToken(newRefresh, newExpiredAt);
        refreshTokenRepository.save(saved);

        return new TokenResponse(newAccess, newRefresh);
    }

    /* =========================
       로그아웃
       ========================= */
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.findByUserEmail(email)
                .ifPresent(refreshTokenRepository::delete);
    }

    /* =========================
       이메일 찾기
       ========================= */
    public FindEmailResponse findEmail(FindEmailRequest req) {

        User user = userRepository
                .findByUserNameAndUserBirth(req.getUserName(), req.getUserBirth())
                .orElseThrow(() ->
                        new RuntimeException("일치하는 계정을 찾을 수 없습니다.")
                );

        return new FindEmailResponse(user.getEmail());
    }

    /* =========================
       비밀번호 재설정 (메일은 나중)
       ========================= */
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {

        User user = userRepository
                .findByUserNameAndEmail(req.getUserName(), req.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("일치하는 계정을 찾을 수 없습니다.")
                );

        String tempPassword = "temp1234";
        user.changePassword(passwordEncoder.encode(tempPassword));
    }

    /* =========================
       중복 체크
       ========================= */
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
