package fi.eerosalla.web.tagger.model.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TagIdsForm {

    @NotNull
    @Size(min = 1, max = 128)
    private List<Integer> tagIds;

}
