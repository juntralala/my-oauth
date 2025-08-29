package dev.juntralala.oauth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "grant_types")
public class GrantType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String clientId;

    @OneToMany(mappedBy = "grantType", fetch = FetchType.LAZY)
    private Set<AuthClientGrantType> authClientGrantTypes;

}
