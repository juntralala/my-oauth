package dev.juntralala.oauth.dto.reuqest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorizeRequest {

    private String response_type;

    private String client_id;

    private String redirect_uri;

    private String scope;

    private String state;

    private String code_challenge;

    private String code_challenge_method;
}
