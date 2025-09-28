package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.entity.AuthorizationCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, String> {

    Optional<AuthorizationCode> findFirstByCode(String code);

    @Transactional
    int deleteByExpiresAtBefore(Instant time);
}
