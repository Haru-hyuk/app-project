package com.flower.service;

import com.flower.config.jwt.JwtTokenProvider;
import com.flower.dto.auth.*;
import com.flower.entity.RefreshToken;
import com.flower.entity.User;
import com.flower.exception.CustomException;
import com.flower.exception.ErrorCode;
import com.flower.repository.RefreshTokenRepository;
import com.flower.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    /* =========================
       회원가입
       ========================= */
    @Transactional
    public void signup(SignupRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (userRepository.existsByNickname(req.getNickname())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
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
                        new CustomException(ErrorCode.AUTH_UNAUTHORIZED)
                );

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String accessToken =
                jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken =
                jwtTokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken tokenEntity = refreshTokenRepository
                .findByUserEmail(user.getEmail())
                .orElse(
                        RefreshToken.builder()
                                .userEmail(user.getEmail())
                                .token(refreshToken)
                                .build()
                );

        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    /* =========================
       Refresh 토큰 재발급
       ========================= */
    @Transactional
    public TokenResponse refresh(String refreshToken) {

        // 1️⃣ Refresh Token JWT 검증 (exp 포함)
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        // 2️⃣ DB에 저장된 토큰과 비교
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.AUTH_INVALID_TOKEN)
                );

        User user = userRepository.findByEmail(saved.getUserEmail())
                .orElseThrow(() ->
                        new CustomException(ErrorCode.AUTH_UNAUTHORIZED)
                );

        // 3️⃣ 새 토큰 발급
        String newAccess =
                jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefresh =
                jwtTokenProvider.generateRefreshToken(user.getEmail());

        saved.updateToken(newRefresh);
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
                .findByUserNameAndUserBirth(
                        req.getUserName(),
                        req.getUserBirth()
                )
                .orElseThrow(() ->
                        new CustomException(ErrorCode.INVALID_REQUEST)
                );

        return new FindEmailResponse(user.getEmail());
    }

    /* =========================
       비밀번호 재설정
       ========================= */
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {

        User user = userRepository
                .findByUserNameAndEmail(
                        req.getUserName(),
                        req.getEmail()
                )
                .orElseThrow(() ->
                        new CustomException(ErrorCode.INVALID_REQUEST)
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
