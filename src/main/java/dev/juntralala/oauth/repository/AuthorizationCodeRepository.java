package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.entity.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, String> {

    Optional<AuthorizationCode> findFirstByCode(String code);


}
