package fi.eerosalla.web.tagger.model.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateForm {

    @NotNull
    @Size(min = 1, max = 256)
    @Pattern(regexp = "^[a-z0-9_]+$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String username;

    @NotNull
    @Size(min = 1, max = 256)
    private String password;

}
