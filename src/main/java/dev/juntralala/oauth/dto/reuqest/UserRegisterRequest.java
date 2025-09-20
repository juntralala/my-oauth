package dev.juntralala.oauth.dto.reuqest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserRegisterRequest {

    @Size(min = 4, max = 255)
    private String username;

    @Size(min = 4, max = 255)
    private String nickname;

    @Size(min = 4)
    private String password;

    @Size(min = 4)
    private String repeatedPassword;

}
