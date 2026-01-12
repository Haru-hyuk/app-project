package com.flower.repository;

import com.flower.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    /** 이메일 기준 refresh token 조회 */
    Optional<RefreshToken> findByUserEmail(String userEmail);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    
}
