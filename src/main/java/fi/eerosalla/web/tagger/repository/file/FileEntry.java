package fi.eerosalla.web.tagger.repository.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "fileseq")
@Table(name = "tg_files")
public class FileEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @Column
    private String name;

    // jpg/png/gif etc.
    @Column
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
