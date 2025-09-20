package dev.juntralala.oauth.dto;

import dev.juntralala.oauth.entity.Permission;
import dev.juntralala.oauth.entity.User;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserPrincipal implements UserDetails, CredentialsContainer, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final User user;

    private final Set<GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.user = user;
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        user.getPermissions()
                .stream()
                .map(Permission::getName)
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        this.authorities = authorities;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void eraseCredentials() {
        getUser().setPassword(null);
    }
}
