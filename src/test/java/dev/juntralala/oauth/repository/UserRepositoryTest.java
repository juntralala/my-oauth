package dev.juntralala.oauth.repository;

import dev.juntralala.oauth.App;
import dev.juntralala.oauth.dto.UserPrincipal;
import dev.juntralala.oauth.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = App.class)
public class UserRepositoryTest {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    @Disabled("bekas coba-coba doang")
    public void select() {
        User user = userRepository.findFirstByUsername("dummy").get();
        Assertions.assertNotNull(user);
    }

}
