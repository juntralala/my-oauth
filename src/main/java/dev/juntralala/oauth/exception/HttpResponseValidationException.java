package dev.juntralala.oauth.exception;

import lombok.Getter;
import org.springframework.util.MultiValueMap;

public class HttpResponseValidationException extends RuntimeException {

    @Getter
    private final MultiValueMap<String, String> errors;

    public HttpResponseValidationException(String message, MultiValueMap<String, String> errors) {
        super(message);
        this.errors = errors;
    }

}
