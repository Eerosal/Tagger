package fi.eerosalla.web.tagger.repository.user;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(
    initialValue = 1,
    name = "idgen",
    sequenceName = "userseq"
)
@Table(name = "tg_users")
public class UserEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @DatabaseField(uniqueIndexName = "idx_user_username")
    private String username;

    @DatabaseField
    private String passwordHash;

    @DatabaseField(indexName = "idx_user_role")
    private String role;

}
