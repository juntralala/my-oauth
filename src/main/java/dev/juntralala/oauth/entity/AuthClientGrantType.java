package dev.juntralala.oauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Struct;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "auth_clients_grant_types")
public class AuthClientGrantType {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class AuthClientGrantTypeId {

        private String authClientId;

        private String grantTypeId;

    }

    @EmbeddedId
    private AuthClientGrantTypeId authClientGrantTypeId;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "auth_client_id", referencedColumnName = "id")
    @MapsId("authClientId")
    private AuthClient authClient;

    @ManyToOne
    @JoinColumn(name = "grant_type_id", referencedColumnName = "id")
    @MapsId("grantTypeId")
    private GrantType grantType;

    private void createAuthClientGrantTypeIdIfNull() {
        if (this.authClientGrantTypeId == null) {
            this.authClientGrantTypeId = new AuthClientGrantTypeId();
        }
    }

    public void setAuthClientId(String authClientId) {
        this.createAuthClientGrantTypeIdIfNull();
        this.authClientGrantTypeId.setAuthClientId(authClientId);
    }

    public void setGrantTypeId(String grantTypeId) {
        this.createAuthClientGrantTypeIdIfNull();
        this.authClientGrantTypeId.setGrantTypeId(grantTypeId);
    }
}