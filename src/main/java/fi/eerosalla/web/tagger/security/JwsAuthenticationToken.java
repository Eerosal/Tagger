package fi.eerosalla.web.tagger.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwsAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final int userId;

    public JwsAuthenticationToken(
        final int userId,
        final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        super.setAuthenticated(true);

        this.userId = userId;
    }

    @Override
    public Object getCredentials() {
        return this.userId;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }
}
