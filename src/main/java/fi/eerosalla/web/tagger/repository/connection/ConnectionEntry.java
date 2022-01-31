package fi.eerosalla.web.tagger.repository.connection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(
    initialValue = 1,
    name = "idgen",
    sequenceName = "connectionseq"
)
@Table(name = "tg_connections", indexes = {
    @Index(name = "idx_connections_tagId", columnList = "tagId"),
    @Index(name = "idx_connections_fileId", columnList = "fileId")
})
public class ConnectionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @Column
    private Integer tagId;

    @Column
    private Integer fileId;

}
