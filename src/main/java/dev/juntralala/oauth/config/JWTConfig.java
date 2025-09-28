package dev.juntralala.oauth.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
public class JWTConfig implements ResourceLoaderAware {

    @Value("${jwt.key.public.path}")
    private String publicKeyPath;

    @Value("${jwt.key.private.path}")
    private String privateKeyPath;

    private ResourceLoader resourceLoader;

    private final Base64.Decoder decoder = Base64.getDecoder();

    private final KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    public JWTConfig() throws NoSuchAlgorithmException {
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public RSAPublicKey loadPublicKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(path);
        String publicKeyString = resource
                .getContentAsString(StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] publickeyBytes = decoder.decode(publicKeyString.getBytes(StandardCharsets.UTF_8));
        KeySpec spec = new X509EncodedKeySpec(publickeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(spec);
        return (RSAPublicKey) publicKey;
    }


    public RSAPrivateKey loadPrivateKey(String privateKeyPath) throws IOException, InvalidKeySpecException {
        Resource privateKeyResource = resourceLoader.getResource(privateKeyPath);
        String privateKeyString = privateKeyResource
                .getContentAsString(StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] privateKeyBytes = decoder.decode(privateKeyString);
        KeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(spec);
        return (RSAPrivateKey) privateKey;
    }

    @Bean
    public JWSVerifier jwsVerifier() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return new RSASSAVerifier(this.loadPublicKey(publicKeyPath));
    }

    @Bean
    public JWSSigner jwsSigner() throws IOException, InvalidKeySpecException {
        return new RSASSASigner(this.loadPrivateKey(privateKeyPath));
    }

    @Bean
    public JWSHeader jwsHeader() {
        return new JWSHeader
                .Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
    }
}
