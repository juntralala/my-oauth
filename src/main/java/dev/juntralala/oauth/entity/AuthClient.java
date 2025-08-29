package dev.juntralala.oauth.entity;

import dev.juntralala.oauth.entity.converter.URIStringConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "auth_clients")
public class AuthClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String clientId;

    @Convert(converter = URIStringConverter.class)
    private URI redirectUri;

    private String defaultScopes = "profile";

    private String clientSecret;

    private Boolean isConfidential = false;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    @OneToMany(mappedBy = "authClient")
    Set<AuthClientGrantType> authClientGrantType;

    @OneToMany(mappedBy = "authClient", fetch = FetchType.LAZY)
    private List<AuthorizationCode> authorizationCodes;

    @OneToMany(mappedBy = "authClient", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

}
