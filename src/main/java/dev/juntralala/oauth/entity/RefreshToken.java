package dev.juntralala.oauth.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "auth_client_id", referencedColumnName = "id")
    private AuthClient authClient;

    private String token;

    private Instant lastUsedAt;

    @CreationTimestamp
    private Instant createdAt;

    private Instant revokedAt;
}
