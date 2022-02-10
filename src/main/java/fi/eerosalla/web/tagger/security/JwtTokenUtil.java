package fi.eerosalla.web.tagger.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final JwtParser PARSER =
        Jwts.parserBuilder().setSigningKey(KEY).build();

    public Jws<Claims> validateToken(final String jwsToken) {
        try {
            return PARSER.parseClaimsJws(jwsToken);
        } catch (Exception ignored) {
        }

        return null;
    }

    // TODO: expiration
    public String createToken(final String userIdStr) {
        return Jwts.builder()
            .setSubject(
                userIdStr
            )
            .setExpiration(
                new Date(
                    System.currentTimeMillis() + 60L * 2L * 1000L
                )
            )
            .signWith(KEY)
            .compact();
    }

}
