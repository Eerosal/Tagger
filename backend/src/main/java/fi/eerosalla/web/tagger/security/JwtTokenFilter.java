package fi.eerosalla.web.tagger.security;

import fi.eerosalla.web.tagger.repository.user.UserEntry;
import fi.eerosalla.web.tagger.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    @NotNull final HttpServletResponse response,
                                    @NotNull final FilterChain chain)
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

        JwtAuthenticationToken token =
            new JwtAuthenticationToken(
                userId,
                RoleAuthority.getAuthoritiesForRole(
                    user.getRole()
                )
            );


        token.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext()
                .setAuthentication(token);

        chain.doFilter(request, response);
    }
}
