package dev.juntralala.oauth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {

    private String refreshToken;

    private String accessToken;

    private String tokenType;

    private String idToken;

    private Long expireIn;

}
