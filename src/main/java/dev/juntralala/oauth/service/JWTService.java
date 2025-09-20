package dev.juntralala.oauth.service;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import static com.nimbusds.jose.JOSEObjectType.JWT;

@Service
public class JWTService {

    private final JWSVerifier jwsVerifier;

    private final JWSSigner jwsSigner;

    private final JWSHeader jwsHeader;

    public JWTService(JWSVerifier jwsVerifier,
                      JWSSigner jwsSigner) {
        this.jwsVerifier = jwsVerifier;
        this.jwsSigner = jwsSigner;
        this.jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JWT)
                .build();
    }

    public boolean verify(String token) throws ParseException, JOSEException {
        return SignedJWT
                .parse(token)
                .verify(jwsVerifier);
    }

    public String createJwt(JWTClaimsSet claims) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claims);
        signedJWT.sign(jwsSigner);
        return signedJWT.serialize();
    }
}
