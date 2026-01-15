package com.flower.repository;

import com.flower.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
extends JpaRepository<RefreshToken, String> {

Optional<RefreshToken> findByUserEmail(String userEmail);

Optional<RefreshToken> findByToken(String token);
}
