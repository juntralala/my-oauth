package dev.juntralala.oauth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizeResponse {

    private String code;

    private String state;

    public AuthorizeResponse(String code) {
        this(code, null);
    }
}
