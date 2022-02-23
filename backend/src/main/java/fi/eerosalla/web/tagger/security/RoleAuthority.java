package fi.eerosalla.web.tagger.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleAuthority implements GrantedAuthority {

    public static final RoleAuthority ADMIN =
        new RoleAuthority("ADMIN");

    public static List<? extends GrantedAuthority> getAuthoritiesForRole(
        final String role
    ) {
        if (role == null) {
            return new ArrayList<>();
        }

        switch (role.toUpperCase()) {
            case "ADMIN":
                return List.of(ADMIN);
            default:
                break;
        }

        return new ArrayList<>();
    }

    private final String authority;

}
