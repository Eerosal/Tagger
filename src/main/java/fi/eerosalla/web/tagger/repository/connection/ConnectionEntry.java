package fi.eerosalla.web.tagger.repository.connection;

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
@SequenceGenerator(
    initialValue = 1,
    name = "idgen",
    sequenceName = "connectionseq"
)
public class ConnectionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @DatabaseField(indexName = "idx_connections_tagId")
    private Integer tagId;

    @DatabaseField(indexName = "idx_connections_fileId")
    private Integer fileId;

}
