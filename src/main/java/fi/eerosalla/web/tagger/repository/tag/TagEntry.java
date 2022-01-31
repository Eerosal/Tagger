package fi.eerosalla.web.tagger.repository.tag;

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
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "tagseq")
@Table(name = "tg_tags", indexes = {
        @Index(name = "idx_tags_name", columnList = "name", unique = true)
})
public class TagEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @Column(length = 32)
    private String name;

}
