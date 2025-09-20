package dev.juntralala.oauth.integration;

import dev.juntralala.oauth.App;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = App.class, webEnvironment = RANDOM_PORT)
public class HelloControllerTest {

    private RestClient restClient;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    public void setUp() {
        restClient = RestClient.create("http://localhost:" + port + "/");
    }

    @Test
    public void sapaSuccess() {
        ResponseEntity<Void> response = restClient.get().uri("/sapa").retrieve().toBodilessEntity();
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

}
