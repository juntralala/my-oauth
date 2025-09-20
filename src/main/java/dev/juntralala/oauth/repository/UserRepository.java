package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findFirstByUsername(String username);

}
