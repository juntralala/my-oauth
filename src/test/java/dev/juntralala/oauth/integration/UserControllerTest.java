package dev.juntralala.oauth.integration;

import dev.juntralala.oauth.App;
import dev.juntralala.oauth.dto.reuqest.UserRegisterRequest;
import dev.juntralala.oauth.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Random;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = App.class, webEnvironment = RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private Integer port;

    private RestClient restClient;

    private UserRepository userRepository;

    @Autowired
    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void registerSuccess() {
        int randomNumber = new Random().nextInt();
        UserRegisterRequest registerRequest = new UserRegisterRequest("user-" + randomNumber, "Usual User", "mypassword", "mypassword");

        ResponseEntity<Void> response = restClient.post()
                .uri(URI.create("http://localhost:" + port + "/register"))
                .body(registerRequest)
                .retrieve().toBodilessEntity();

        Assertions.assertEquals(201, response.getStatusCode().value());

        userRepository.deleteAll();
    }

}
