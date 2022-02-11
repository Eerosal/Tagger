package fi.eerosalla.web.tagger.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthorizeResponse {

    // JWS token for APIs
    private String token;

}
