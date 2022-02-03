package fi.eerosalla.web.tagger.repository.tag;

import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "tagseq")
public class TagEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @DatabaseField(uniqueIndexName = "idx_tags_name")
    private String name;

}
