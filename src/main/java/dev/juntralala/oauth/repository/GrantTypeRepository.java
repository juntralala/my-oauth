package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.entity.GrantType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrantTypeRepository extends JpaRepository<GrantType, String> {
}
