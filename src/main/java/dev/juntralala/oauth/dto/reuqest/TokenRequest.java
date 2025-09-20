package dev.juntralala.oauth.dto.reuqest;

import dev.juntralala.oauth.validation.constraint.OneOfField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@OneOfField(fields = {"code", "refresh_token"})
public class TokenRequest {

    @NotNull
    @NotBlank
    private String grant_type;

    private String code;

    private String refresh_token;

    @NotNull
    @NotBlank
    private String client_id;

    @NotNull
    @NotBlank
    private String redirect_uri;


    @NotBlank
    private String code_verifier;

}
