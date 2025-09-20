package dev.juntralala.oauth.controller;

import dev.juntralala.oauth.dto.UserPrincipal;
import dev.juntralala.oauth.dto.reuqest.UserRegisterRequest;
import dev.juntralala.oauth.entity.User;
import dev.juntralala.oauth.exception.ValidationException;
import dev.juntralala.oauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Void> register(HttpServletRequest request,
                                           @Valid @ModelAttribute UserRegisterRequest registerRequest) throws ValidationException {
        User user = userService.register(registerRequest);
        UserDetails userDetails = new UserPrincipal(user);

        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
        SecurityContextHolder.setContext(securityContext);

        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext
        );

        return ResponseEntity
                .created(URI.create("/"))
                .build();
    }

}
