package dev.juntralala.oauth.integration;

import dev.juntralala.oauth.App;
import dev.juntralala.oauth.dto.reuqest.UserRegisterRequest;
import dev.juntralala.oauth.entity.User;
import dev.juntralala.oauth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = App.class, webEnvironment = RANDOM_PORT)
public class LoginTest {

    @LocalServerPort
    private Integer port;

    private RestClient restClient;

    private UserService userService;

    @BeforeEach
    public void setRestClient() {
        this.restClient = RestClient.create("http://localhost:" + port);
    }

    @Autowired
    public  void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Test
    public void loginSuccess() {
        User registeredUser = userService.register(new UserRegisterRequest("user-1221", "Usual User", "my_password", "my_passwaord"));
        assertNotNull(registeredUser);

        var loginRequest = MultiValueMap.fromSingleValue(Map.of(
                "username", registeredUser.getUsername(),
                "password", registeredUser.getPassword()
        ));

        ResponseEntity<Void> response = restClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(loginRequest)
                .retrieve()
                .toBodilessEntity();

        assertThat("Http status code must be redirection", response.getStatusCode().is3xxRedirection());
    }
}
