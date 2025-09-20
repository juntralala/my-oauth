package dev.juntralala.oauth.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import dev.juntralala.oauth.dto.RestResponse;
import dev.juntralala.oauth.dto.UserPrincipal;
import dev.juntralala.oauth.dto.response.AuthorizeResponse;
import dev.juntralala.oauth.dto.response.TokenResponse;
import dev.juntralala.oauth.dto.reuqest.AuthorizeRequest;
import dev.juntralala.oauth.dto.reuqest.TokenRequest;
import dev.juntralala.oauth.entity.User;
import dev.juntralala.oauth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    private final AuthenticationService authenticationService;

    private final ObjectMapper objectMapper;

    public AuthController(AuthenticationService authenticationService, ObjectMapper objectMapper) {
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/authorize")
    public ResponseEntity<Void> getAuthToken(
            @ModelAttribute AuthorizeRequest authorizeRequest,
            @AuthenticationPrincipal UserPrincipal userDetails,
            HttpSession session,
            HttpServletRequest request
    ) throws NoSuchAlgorithmException {

        // apakah sudah di konfirmasi pengguna
        Boolean authorized = (Boolean) Optional.ofNullable(session.getAttribute("authorized"))
                .orElse(false);
        session.removeAttribute("authorized");
        if (!authorized) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/authorize/confirm");
            request.getParameterMap().forEach((key, values) -> {
                for (String value : values) {
                    builder.queryParam(key, value);
                }
            });

            return ResponseEntity.status(302)
                    .location(builder.build().encode().toUri())
                    .build();
        }

        User user = userDetails.getUser();
        AuthorizeResponse authorizeResponses = authenticationService.authorize(authorizeRequest, user);

        URI redirectUri = UriComponentsBuilder
                .fromUriString(authorizeRequest.getRedirect_uri())
                .queryParams(MultiValueMap.fromSingleValue(objectMapper.convertValue(authorizeResponses, new TypeReference<Map<String, String>>() {
                })))
                .build()
                .toUri();

        return ResponseEntity
                .status(302)
                .location(redirectUri)
                .build();
    }

    @PostMapping(path = "/authorize/confirm")
    public ResponseEntity<Void> confirmAuthorize(
            HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute("authorized", Boolean.TRUE);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        request.getParameterMap().forEach((key, values) -> {
            for (String value : values) {
                params.add(key, value);
            }
        });

        URI location = UriComponentsBuilder
                .fromUriString("/authorize")
                .queryParams(params)
                .build().encode().toUri();

        return ResponseEntity.status(302)
                .location(location)
                .build();
    }

    @PostMapping(path = "/token")
    public ResponseEntity<RestResponse<TokenResponse>> getToken(@ModelAttribute TokenRequest tokenRequest) throws NoSuchAlgorithmException, JOSEException {
        TokenResponse tokenResponse = authenticationService.getToken(tokenRequest);

        return ResponseEntity.ok(
                RestResponse.<TokenResponse>builder().body(tokenResponse).build()
        );
    }

}
