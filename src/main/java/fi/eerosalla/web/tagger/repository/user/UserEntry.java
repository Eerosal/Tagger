package fi.eerosalla.web.tagger.repository.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "userseq")
@Table(name = "tg_users")
public class UserEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    private Integer id;

    @Column
    private String username;

    @Column
    private String passwordHash;

    @Column
    private String role;

}
