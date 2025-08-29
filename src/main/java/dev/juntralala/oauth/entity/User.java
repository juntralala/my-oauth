package dev.juntralala.oauth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    public User() {
        this.permissions = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nickname;

    private String username;

    private String password;

    private String profilePicture;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "users_permissions",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private Set<Permission> permissions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AuthorizationCode> authorizationCodes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

}
