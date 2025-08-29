package dev.juntralala.oauth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "authorizationCodes")
public class AuthorizationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "auth_client_id", referencedColumnName = "id")
    private AuthClient authClient;

    private String code;

    private String scopes;

    @CreationTimestamp
    private Instant createdAt;

    private Instant expiresAt;

    private Instant usedAt;

    private String codeChallenger;

    private String codeChallengerMethod;
}
