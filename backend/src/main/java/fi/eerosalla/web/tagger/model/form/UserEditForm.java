package fi.eerosalla.web.tagger.model.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserEditForm {

    @Nullable
    @Size(min = 1, max = 256)
    @Pattern(regexp = "^[a-z0-9_]+$", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String username;

    @Nullable
    @Size(min = 1, max = 256)
    private String password;

}
