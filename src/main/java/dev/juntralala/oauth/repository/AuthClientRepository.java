package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.entity.AuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthClientRepository extends JpaRepository<AuthClient, String> {

    Optional<AuthClient> findFirstByClientId(String clientId);

}
