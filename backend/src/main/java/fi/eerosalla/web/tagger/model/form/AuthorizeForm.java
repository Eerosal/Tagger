package fi.eerosalla.web.tagger.model.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class AuthorizeForm {

    @NotNull
    @Size(min = 1, max = 256)
    private String username;

    @NotNull
    @Size(min = 1, max = 256)
    private String password;

}
