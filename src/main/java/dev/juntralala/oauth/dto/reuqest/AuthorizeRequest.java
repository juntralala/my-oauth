package dev.juntralala.oauth.dto.reuqest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorizeRequest {

    @NotNull
    private String response_type;

    @NotNull
    private String client_id;

    @NotNull
    private String redirect_uri;

    @NotBlank
    private String scope;

    private String state;

    private String code_challenge;

    private String code_challenge_method;
}
