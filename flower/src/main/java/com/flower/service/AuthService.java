package com.flower.service;

import com.flower.config.jwt.JwtTokenProvider;
import com.flower.dto.auth.*;
import com.flower.entity.RefreshToken;
import com.flower.entity.User;
import com.flower.external.kakao.KakaoAuthClient;
import com.flower.external.kakao.KakaoUserInfo;
import com.flower.exception.CustomException;
import com.flower.exception.ErrorCode;
import com.flower.repository.FavoriteRepository;
import com.flower.repository.RefreshTokenRepository;
import com.flower.repository.SearchHistoryRepository;
import com.flower.repository.UserRepository;
import com.flower.repository.ViewHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FavoriteRepository favoriteRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final KakaoAuthClient kakaoAuthClient;

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
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        return issueTokens(user.getEmail());
    }

    /* =========================
       Social Login (Kakao)
       ========================= */
    @Transactional
    public TokenResponse socialLogin(SocialLoginRequest req) {

        if (req == null || req.getProvider() == null || req.getAccessToken() == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        if (!"kakao".equalsIgnoreCase(req.getProvider())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        KakaoUserInfo userInfo;
        try {
            userInfo = kakaoAuthClient.getUserInfo(req.getAccessToken());
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        if (userInfo == null || userInfo.getId() == null) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String email = resolveEmail(userInfo);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createSocialUser(userInfo, email));

        return issueTokens(user.getEmail());
    }

    /* =========================
       Refresh 토큰 재발급
       ========================= */
    @Transactional
    public TokenResponse refresh(String refreshToken) {

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_INVALID_TOKEN));

        User user = userRepository.findByEmail(saved.getUserEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED));

        String newAccess = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefresh = jwtTokenProvider.generateRefreshToken(user.getEmail());

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
                .findByUserNameAndUserBirth(req.getUserName(), req.getUserBirth())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        return new FindEmailResponse(user.getEmail());
    }

    /* =========================
       비밀번호 재설정
       ========================= */
    @Transactional
    public void resetPassword(ResetPasswordRequest req) {

        User user = userRepository
                .findByUserNameAndEmail(req.getUserName(), req.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

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

    /* =========================
       회원 탈퇴
       ========================= */
    @Transactional
    public void deleteAccount(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        Integer userId = user.getUserId();

        favoriteRepository.deleteByUserId(userId);
        searchHistoryRepository.deleteByUserId(userId);
        viewHistoryRepository.deleteByUserId(userId);

        refreshTokenRepository.findByUserEmail(email)
                .ifPresent(refreshTokenRepository::delete);

        userRepository.delete(user);
    }

    /* =========================
       내부 메서드
       ========================= */
    private TokenResponse issueTokens(String email) {

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        RefreshToken tokenEntity = refreshTokenRepository
                .findByUserEmail(email)
                .orElse(
                        RefreshToken.builder()
                                .userEmail(email)
                                .token(refreshToken)
                                .build()
                );

        tokenEntity.updateToken(refreshToken);
        refreshTokenRepository.save(tokenEntity);

        return new TokenResponse(accessToken, refreshToken);
    }

    private User createSocialUser(KakaoUserInfo userInfo, String email) {

        String nickname = resolveNickname(userInfo);
        String userName = resolveUserName(userInfo, nickname);
        String userBirth = resolveUserBirth(userInfo);
        String profileImage = resolveProfileImage(userInfo);

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .nickname(nickname)
                .userName(userName)
                .userBirth(userBirth)
                .oauthProvider("kakao")
                .profileImage(profileImage)
                .build();

        return userRepository.save(user);
    }

    private String resolveEmail(KakaoUserInfo userInfo) {
        return "kakao_" + userInfo.getId() + "@kakao.local";
    }

    private String resolveNickname(KakaoUserInfo userInfo) {

        if (userInfo.getKakaoAccount() != null
                && userInfo.getKakaoAccount().getProfile() != null) {

            String nickname = userInfo.getKakaoAccount().getProfile().getNickname();
            if (hasText(nickname)) {
                return nickname;
            }
        }
        return "kakao_" + userInfo.getId();
    }

    private String resolveUserName(KakaoUserInfo userInfo, String fallback) {
        return fallback;
    }

    private String resolveUserBirth(KakaoUserInfo userInfo) {
        return "00000000";
    }

    private String resolveProfileImage(KakaoUserInfo userInfo) {

        if (userInfo.getKakaoAccount() != null
                && userInfo.getKakaoAccount().getProfile() != null) {
            return userInfo.getKakaoAccount().getProfile().getProfileImageUrl();
        }
        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
