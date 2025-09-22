package dev.juntralala.oauth.dto.reuqest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Size(min = 1, max = 255)
    private String username;

    @Size(min = 1, max = 255)
    private String password;

}
