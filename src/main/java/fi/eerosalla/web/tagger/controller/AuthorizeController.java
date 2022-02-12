package fi.eerosalla.web.tagger.controller;

import fi.eerosalla.web.tagger.config.SecurityConfig;
import fi.eerosalla.web.tagger.model.form.AuthorizeForm;
import fi.eerosalla.web.tagger.model.response.AuthorizeResponse;
import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.repository.user.UserEntry;
import fi.eerosalla.web.tagger.repository.user.UserRepository;
import fi.eerosalla.web.tagger.security.JwtAuthenticationToken;
import fi.eerosalla.web.tagger.security.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
public class AuthorizeController {

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;

    public AuthorizeController(final JwtTokenUtil jwtTokenUtil,
                               final UserRepository userRepository,
                               final PasswordEncoder passwordEncoder,
                               final SecurityConfig securityConfig) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityConfig = securityConfig;
    }

    private AuthorizeResponse createAuthorizeResponse(final int userId) {
        int tokenLifetimeSeconds =
            securityConfig.getTokenLifetimeSeconds();

        return new AuthorizeResponse(
            jwtTokenUtil.createToken(
                String.valueOf(userId),
                tokenLifetimeSeconds
            ),
            tokenLifetimeSeconds
        );
    }

    @PostMapping("/authorize")
    public Object authorize(
        final @RequestBody @Validated AuthorizeForm authorizeForm) {

        UserEntry user = userRepository.queryForUsername(
            authorizeForm.getUsername()
        );

        if (user == null) {
            System.out.println("user not found " + authorizeForm.getUsername());
            return new ResponseEntity<>(
                new ErrorResponse("Invalid credentials"),
                HttpStatus.UNAUTHORIZED
            );
        }

        if (!passwordEncoder.matches(
            authorizeForm.getPassword(),
            user.getPasswordHash()
        )) {

            System.out.println("invalid hash " + user.getPasswordHash());
            return new ResponseEntity<>(
                new ErrorResponse("Invalid credentials"),
                HttpStatus.UNAUTHORIZED
            );
        }

        return createAuthorizeResponse(user.getId());
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/renew-token")
    public Object renewToken() {
        JwtAuthenticationToken token =
            (JwtAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        return createAuthorizeResponse(token.getUserId());
    }

}
