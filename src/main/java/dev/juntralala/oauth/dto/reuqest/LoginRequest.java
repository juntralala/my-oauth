package dev.juntralala.oauth.dto.reuqest;

import jakarta.validation.constraints.Size;

public class LoginRequest {

    @Size(min = 1, max = 255)
    public String username;

    @Size(min = 1, max = 255)
    public String password;

}
