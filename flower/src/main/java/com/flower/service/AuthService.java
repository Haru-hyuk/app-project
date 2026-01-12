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

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken token = refreshTokenRepository
                .findByUserEmail(user.getEmail())
                .orElse(
                        RefreshToken.builder()
                                .userEmail(user.getEmail())
                                .refreshToken(refreshToken)
                                .build()
                );

        token.updateToken(refreshToken);
        refreshTokenRepository.save(token);

        return new TokenResponse(accessToken, refreshToken);
    }

    /* =========================
       Refresh 토큰 재발급
       ========================= */
    @Transactional
    public TokenResponse refresh(String refreshToken) {

        RefreshToken saved = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new RuntimeException("리프레시 토큰이 유효하지 않습니다.")
                );

        String email = saved.getUserEmail();

        String newAccess = jwtTokenProvider.generateAccessToken(email);
        String newRefresh = jwtTokenProvider.generateRefreshToken(email);

        saved.updateToken(newRefresh);
        refreshTokenRepository.save(saved);

        return new TokenResponse(newAccess, newRefresh);
    }

    /* =========================
       로그아웃
       ========================= */
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.deleteById(email);
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

        // 지금은 메일 없이 임시 비밀번호 로직만 준비
        String tempPassword = "temp1234";

        user.changePassword(passwordEncoder.encode(tempPassword));
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
