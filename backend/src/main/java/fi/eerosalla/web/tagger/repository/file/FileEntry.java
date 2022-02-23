package fi.eerosalla.web.tagger.repository.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(
    initialValue = 1,
    name = "idgen",
    sequenceName = "fileseq"
)
@Table(name = "tg_files")
public class FileEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @DatabaseField
    private String name;

    // jpg/png/gif etc.
    @DatabaseField
    private String extension;

    @JsonIgnore
    public String getInternalFilename() {
        return this.getId() + "." + this.getExtension();
    }

    @JsonIgnore
    public String getThumbnailFilename() {
        return this.getId() + "_thumbnail.jpg";
    }
}
