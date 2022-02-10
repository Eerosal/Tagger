package fi.eerosalla.web.tagger.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {

    private String authority;

}
