package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findFirstByToken(String refreshToken);

}
