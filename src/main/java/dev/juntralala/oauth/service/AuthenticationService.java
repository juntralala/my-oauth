package dev.juntralala.oauth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import dev.juntralala.oauth.dto.response.AuthorizeResponse;
import dev.juntralala.oauth.dto.response.TokenResponse;
import dev.juntralala.oauth.dto.reuqest.AuthorizeRequest;
import dev.juntralala.oauth.dto.reuqest.TokenRequest;
import dev.juntralala.oauth.entity.*;
import dev.juntralala.oauth.exception.HttpResponseValidationException;
import dev.juntralala.oauth.repository.AuthClientRepository;
import dev.juntralala.oauth.repository.AuthorizationCodeRepository;
import dev.juntralala.oauth.repository.GrantTypeRepository;
import dev.juntralala.oauth.repository.RefreshTokenRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class AuthenticationService {

    private final GrantTypeRepository grantTypeRepository;

    private final AuthorizationCodeRepository authorizationCodeRepository;

    private final AuthClientRepository authClientRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JWTService jwtService;

    private final List<String> supportedCodeChallengeMethods = List.of(
            "s256",
            "plain"
    );

    public AuthenticationService(
            AuthorizationCodeRepository authorizationCodeRepository,
            AuthClientRepository authClientRepository,
            GrantTypeRepository grantTypeRepository,
            RefreshTokenRepository refreshTokenRepository,
            JWTService jwtService) {
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.authClientRepository = authClientRepository;
        this.grantTypeRepository = grantTypeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    private AuthClient validateAuthClientExists(String clientId, MultiValueMap<String, String> errors) {
        Optional<AuthClient> authClientOptional = authClientRepository.findFirstByClientId(clientId);
        if (authClientOptional.isEmpty()) {
            errors.add("auth_client", "Auth client id not registered");
        }
        return authClientOptional.orElseThrow();
    }

    private AuthorizationCode validateAuthorizationCodeExists(String code, MultiValueMap<String, String> errors) {
        Optional<AuthorizationCode> authorizationCodeOptional = authorizationCodeRepository.findFirstByCode(code);
        if (authorizationCodeOptional.isEmpty()) {
            errors.add("code", "The code doesn't exists");
        }
        return authorizationCodeOptional.orElseThrow();
    }

    private void validateCodeChallengeMethodSupported(String codeChallengeMethod, MultiValueMap<String, String> errors) {
        if (supportedCodeChallengeMethods.contains(codeChallengeMethod.toLowerCase())) {
            errors.add("code_challenger_method", "code_challenger_method is not supported");
        }
    }

    private void validateGrantTypeExists(String grantTypeName, MultiValueMap<String, String> errors) {
        GrantType grantTypeProbe = GrantType
                .builder()
                .name(grantTypeName)
                .build();
        if (!grantTypeRepository.exists(Example.of(grantTypeProbe))) {
            errors.add("response_type", "The response_type field contains no supported Response Type");
        }
    }

    private RefreshToken validateRefreshTokenExists(String refreshToken, MultiValueMap<String, String> errors) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findFirstByToken(refreshToken);
        if (refreshTokenOptional.isEmpty()) {
            errors.add("refresh_token", "The refresh_token doesn't exists");
        }
        return refreshTokenOptional.orElseThrow();
    }

    public AuthorizeResponse authorize(AuthorizeRequest authorizeRequest, User currentUser) throws NoSuchAlgorithmException {
        // validation
        MultiValueMap<String, String> errors = new LinkedMultiValueMap<>();
        validateGrantTypeExists(authorizeRequest.getResponse_type(), errors);
        AuthClient authClient = validateAuthClientExists(authorizeRequest.getClient_id(), errors);
        if (authorizeRequest.getCode_challenge_method() != null) {
            validateCodeChallengeMethodSupported(authorizeRequest.getCode_challenge_method(), errors);
        }
        if (!errors.isEmpty()) {
            throw new HttpResponseValidationException("Validation Error", errors);
        }

        String codeChallenge = authorizeRequest.getCode_challenge();
        String codeChallengeMethod = authorizeRequest.getCode_challenge_method();
        if (codeChallengeMethod != null && codeChallengeMethod.equalsIgnoreCase("S256")) {
            codeChallenge = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(codeChallenge.getBytes(UTF_8)));
        }

        String code = UUID.randomUUID().toString();
        AuthorizationCode authorizationCode = AuthorizationCode
                .builder()
                .code(code)
                .codeChallenge(codeChallenge)
                .codeChallengeMethod(authorizeRequest.getCode_challenge_method())
                .expiresAt(Instant.now().plus(Duration.ofMinutes(5)))
                .authClient(authClient)
                .scopes("")
                .user(currentUser)
                .build();

        authorizationCodeRepository.save(authorizationCode);

        AuthorizeResponse authorizeResponse = new AuthorizeResponse(code);
        if (authorizeRequest.getState() != null) {
            authorizeResponse.setState(authorizeRequest.getState());
        }
        return authorizeResponse;
    }

    @Transactional
    public TokenResponse getToken(TokenRequest tokenRequest) throws NoSuchAlgorithmException, JOSEException {
        MultiValueMap<String, String> errors = new LinkedMultiValueMap<>();

        validateGrantTypeExists(tokenRequest.getGrant_type(), errors);

        AuthorizationCode authorizationCode = null;
        RefreshToken refreshToken = null;
        if (tokenRequest.getCode() != null) {
            authorizationCode = validateAuthorizationCodeExists(tokenRequest.getCode(), errors);
        } else if (tokenRequest.getRefresh_token() != null) {
            refreshToken = validateRefreshTokenExists(tokenRequest.getRefresh_token(), errors);
        } else {
            throw new RuntimeException("One of fields code or refresh_token must be exists");
        }

        AuthClient authClient;
        if (authorizationCode != null) {
            authClient = authorizationCode.getAuthClient();
        } else if (refreshToken != null) {
            authClient = refreshToken.getAuthClient();
        } else {
            throw new RuntimeException();
        }
        if (authClient.getClientId().equals(tokenRequest.getClient_id())) {
            errors.add("client_id", "client_id must be does not match");
        }

        validateAuthClientExists(tokenRequest.getClient_id(), errors);
        URI redirectUri = URI.create(tokenRequest.getRedirect_uri());
        if (authClient.getRedirectUri().equals(redirectUri)) {
            errors.add("redirect_uri", "redirect_uri not match");
        }

        String codeVerifier = tokenRequest.getCode_verifier();
        String codeChallenge = authorizationCode.getCodeChallenge();
        String codeChallengeMethod = authorizationCode.getCodeChallengeMethod();
        if (codeChallengeMethod.equals("S256")) {
            codeVerifier = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(codeVerifier.getBytes(UTF_8)));
        }
        if (!codeChallenge.equals(codeVerifier)) {
            errors.add("code_challenge", "code_challenge is wrong");
        }
        if (!errors.isEmpty()) {
            throw new HttpResponseValidationException("Validation Error", errors);
        }

        User user = authorizationCode.getUser();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .authClient(authClient)
                .token(UUID.randomUUID().toString())
                .user(user)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .expirationTime(Date.from(Instant.now().plus(5, DAYS)))
                .issuer("localhost")
                .subject(user.getUsername())
                .claim("role", user.getRole().getName())
                .claim("permissions", user.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()))
                .build();
        String accessToken = jwtService.createJwt(claims);

        return new TokenResponse(
                newRefreshToken.getToken(),
                accessToken,
                "Bearer",
                "",
                Duration.ofDays(5).getSeconds()
        );
    }
}
