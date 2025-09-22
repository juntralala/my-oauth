package dev.juntralala.oauth.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.juntralala.oauth.App;
import dev.juntralala.oauth.dto.RestResponse;
import dev.juntralala.oauth.dto.reuqest.UserRegisterRequest;
import dev.juntralala.oauth.repository.AuthorizationCodeRepository;
import dev.juntralala.oauth.repository.RefreshTokenRepository;
import dev.juntralala.oauth.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.security.SecureRandom;
import java.util.List;
import java.util.StringJoiner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@SpringBootTest(classes = App.class, webEnvironment = RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private Integer port;

    private RestClient restClient;

    private UserService userService;

    private RefreshTokenRepository refreshTokenRepository;

    private AuthorizationCodeRepository authorizationCodeRepository;

    private ObjectMapper objectMapper;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAuthorizationCodeRepository(AuthorizationCodeRepository authorizationCodeRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository;
    }

    @Autowired
    public void setRefreshTokenRepository(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @AfterEach
    public void tearDown() {
        refreshTokenRepository.deleteAll();
        authorizationCodeRepository.deleteAll();
        userService.deleteAll();
    }

    @Test
    public void registerSuccess() {
        int randomNumber = random.nextInt();
        StringJoiner joiner = new StringJoiner("&");
        joiner.add("username=" + "user-" + randomNumber);
        joiner.add("nickname=" + "Usual User");
        joiner.add("password=" + "my_password");
        joiner.add("repeatedPassword=" + "my_password");

        ResponseEntity<Void> response = restClient.post()
                .uri(URI.create("http://localhost:" + port + "/register"))
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(joiner.toString())
                .retrieve().toBodilessEntity();

        Assertions.assertEquals(201, response.getStatusCode().value());

        // try accessing protected route
        List<String> setCookies = response.getHeaders().get("Set-Cookie");
        ResponseEntity<Void> response2 = restClient.get().uri("http://localhost:" + port + "/user/current")
                .cookies(cookies -> {
                    if (setCookies != null) {
                        for (String setCookie : setCookies) {
                            String[] part = setCookie.split(";", 2);
                            String[] pairKeyValue = part[0].split("=", 2);
                            if (pairKeyValue.length == 2) {
                                cookies.add(pairKeyValue[0], pairKeyValue[1]);
                            }
                        }
                    }
                })
                .retrieve()
                .toBodilessEntity();
        Assertions.assertEquals(200, response2.getStatusCode().value());
    }

    @Test
    public void registerFailedUsernameBlank() {
        UserRegisterRequest registerRequest = new UserRegisterRequest(
                "",
                "user-1234",
                "my_password",
                "my_password"
        );

        MultiValueMap<String, String> form = MultiValueMap.fromSingleValue(objectMapper.convertValue(registerRequest, new TypeReference<>() {
        }));
        ResponseEntity<RestResponse<Void>> response = restClient.post()
                .uri("http://localhost:" + port + "/register")
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                })
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getErrors());
        Assertions.assertNotNull(response.getBody().getErrors().get("username"));
    }

    @Test
    public void registerFailedUsernameDuplicate() {
        String username = "user-" + random.nextInt();
        Assertions.assertDoesNotThrow(() -> {
            userService.register(UserRegisterRequest.builder()
                    .username(username)
                    .nickname("First User")
                    .password("my_password")
                    .repeatedPassword("my_password")
                    .build());
        });

        UserRegisterRequest registerRequest = new UserRegisterRequest(
                username,
                "Second User",
                "my_password",
                "my_password"
        );
        MultiValueMap<String, String> form = MultiValueMap.fromSingleValue(objectMapper.convertValue(registerRequest, new TypeReference<>() {
        }));

        ResponseEntity<RestResponse<Void>> response = restClient.post()
                .uri("http://localhost:" + port + "/register")
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                })
                .toEntity(new ParameterizedTypeReference<>() {
                });

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getError());
        Assertions.assertEquals("Username already exists", response.getBody().getError());
    }
}
