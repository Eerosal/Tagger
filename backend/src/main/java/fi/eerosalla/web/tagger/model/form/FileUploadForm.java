package fi.eerosalla.web.tagger.model.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class FileUploadForm {

    @NotNull
    @Size(min = 1, max = 128)
    private String filename;

}
