package dev.juntralala.oauth.integration;

import dev.juntralala.oauth.App;
import dev.juntralala.oauth.dto.reuqest.UserRegisterRequest;
import dev.juntralala.oauth.entity.User;
import dev.juntralala.oauth.repository.AuthorizationCodeRepository;
import dev.juntralala.oauth.repository.RefreshTokenRepository;
import dev.juntralala.oauth.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@SpringBootTest(classes = {App.class}, webEnvironment = RANDOM_PORT)
public class AuthorizationControllerTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    private UserService userService;

    private AuthorizationCodeRepository authorizationCodeRepository;

    private RefreshTokenRepository refreshTokenRepository;

    private final Random random = new Random();

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @BeforeEach
    public void setUp() {
        this.restClient = RestClient.create("http://localhost:" + port);
    }

    @AfterEach
    public void tearDown() {
        this.refreshTokenRepository.deleteAll();
        this.authorizationCodeRepository.deleteAll();
        this.userService.deleteAll();
    }

    @Test
    public void authorizeFullFlowSuccess() {
        // add new user and login
        User user = this.userService.register(new UserRegisterRequest("user-" + random.nextInt(), "User Test", "my_password", "my_password"));
        ResponseEntity<Void> response = this.restClient.post()
                .uri("/login")
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(MultiValueMap.fromSingleValue(Map.of("username", user.getUsername(), "password", "my_password")))
                .retrieve()
                .toBodilessEntity();
        HttpStatusCode loginStatusCode = response.getStatusCode();
        assertThat("must be redirection", loginStatusCode.is3xxRedirection());
        List<String> setCookies = response.getHeaders().getOrEmpty("Set-Cookie");
        Map<String, String> cookiesMap = setCookies.stream().collect(Collectors.toMap((setCookie) -> setCookie.split(";")[0].split("=")[0], setCookie -> setCookie.split(";")[0].split("=")[1]));
        assertThat(cookiesMap.size(), greaterThan(0));

        // hits, endpoint /authorize
        Map<String, String> params = Map.of(
                "client_id", "client_id",
                "response_type", "code",
                "redirect_uri", "http://localhost/callback",
                "state", "42f553e54fa295d459fa");
        StringJoiner paramJoiner = new StringJoiner("&")
                .add("client_id={client_id}")
                .add("response_type={response_type}")
                .add("redirect_uri={redirect_uri}")
                .add("scope=openid")
                .add("state={state}");
        ResponseEntity<Void> responseAuthorize1 = restClient.get()
                .uri("/authorize?" + paramJoiner, params)
                .cookies(cookies -> cookies.addAll(MultiValueMap.fromSingleValue(cookiesMap)))
                .retrieve()
                .toBodilessEntity();
        HttpStatusCode authorizeStatusCode = responseAuthorize1.getStatusCode();
        URI location = responseAuthorize1.getHeaders().getLocation();
        assertThat("Authorize status code must be redirection", authorizeStatusCode.is3xxRedirection());
        assertThat(location, notNullValue());
        assertThat(location.getPath(), is("/authorize/confirm"));

        // redirected to authorize/confirm
        ResponseEntity<Void> confirmResponse = restClient.post()
                .uri(location)
                .cookies(cookies -> cookies.addAll(MultiValueMap.fromSingleValue(cookiesMap)))
                .retrieve()
                .toBodilessEntity();
        location = confirmResponse.getHeaders().getLocation();

        assertThat(302, is(confirmResponse.getStatusCode().value()));
        assertThat(location, notNullValue());
        assertThat(location.getPath(), equalTo("/authorize"));

        ResponseEntity<Void> authorizeResponse2 = restClient.get()
                .uri(location)
                .cookies(cookies -> cookies.addAll(MultiValueMap.fromSingleValue(cookiesMap)))
                .retrieve()
                .toBodilessEntity();
        HttpStatusCode authorizeStatusCode2 = authorizeResponse2.getStatusCode();
        location = authorizeResponse2.getHeaders().getLocation();
        assertThat("response must be redirected to callback", authorizeStatusCode2.is3xxRedirection());
        assertThat(location, notNullValue());

        String urlCallback = location.getScheme() + "://" + location.getAuthority() + location.getPath();
        assertThat(urlCallback, is("http://localhost/callback"));

        String[] keyValuePair = location.getQuery().split("=");
        assertThat(keyValuePair[0], is("code"));
        assertThat(keyValuePair[1], instanceOf(String.class));
        assertThat(keyValuePair[1], not(blankString()));
    }

    public void tokenSuccess() {
        User user = userService.register(new UserRegisterRequest("user-" + random.nextInt(), "User Test", "my_password", "my_password"));

    }
}
