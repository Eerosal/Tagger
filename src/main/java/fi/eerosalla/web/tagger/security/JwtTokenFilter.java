package fi.eerosalla.web.tagger.security;

import fi.eerosalla.web.tagger.model.data.Role;
import fi.eerosalla.web.tagger.repository.user.UserEntry;
import fi.eerosalla.web.tagger.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public JwtTokenFilter(final JwtTokenUtil jwtTokenUtil,
                          final UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    public static List<? extends GrantedAuthority> getAuthoritiesForUser(
        final UserEntry user
    ) {
        return user.getRole() != null
            && !user.getRole().isEmpty()
            ? List.of(
            new Role(user.getRole().toUpperCase())
        )
            : new ArrayList<>();
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain)
        throws ServletException, IOException {

        String authorizationHeader =
            request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
            || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            SecurityContextHolder.clearContext();

            chain.doFilter(request, response);

            return;
        }

        String jws = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
        if (jws.length() == 0) {
            SecurityContextHolder.clearContext();

            chain.doFilter(request, response);

            return;
        }

        Jws<Claims> claims = jwtTokenUtil.validateToken(jws);
        if (claims == null) {
            SecurityContextHolder.clearContext();

            chain.doFilter(request, response);

            return;
        }

        int userId = Integer.parseInt(claims.getBody().getSubject());

        UserEntry user = userRepository.queryForId(userId);
        if (user == null) {
            SecurityContextHolder.clearContext();

            chain.doFilter(request, response);

            return;
        }

        JwsAuthenticationToken token =
            new JwsAuthenticationToken(
                userId,
                getAuthoritiesForUser(user)
            );


        token.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();

        securityContext.setAuthentication(token);

        SecurityContextHolder.setContext(securityContext);

        chain.doFilter(request, response);
    }
}
