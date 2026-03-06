package product.orders.uiservice.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a user principal with their details and authorities. Verified user details after authentication.
 */
public class UserPrincipal implements UserDetails {

    private final UUID userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * JWT token given to the user after successful authentication.
     */
    private final String token;

    public UserPrincipal(
            UUID userId,
            String email,
            Collection<? extends GrantedAuthority> authorities,
            String token

    ) {
        this.userId = userId;
        this.email = email;
        this.authorities = authorities;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
    // --- UserDetails methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}