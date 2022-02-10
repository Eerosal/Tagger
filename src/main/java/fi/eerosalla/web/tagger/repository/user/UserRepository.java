package fi.eerosalla.web.tagger.repository.user;

import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import fi.eerosalla.web.tagger.security.AccessRole;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;


@Repository
public class UserRepository extends CrudRepository<UserEntry, Integer> {

    public UserRepository(final ConnectionSource connectionSource,
                          final PasswordEncoder passwordEncoder) {
        super(connectionSource, UserEntry.class);
        // TODO: cache

        UserEntry rootUser = queryForId(1);
        if (rootUser != null) {
            return;
        }

        UserEntry newRootUser = new UserEntry(
            1, "root", passwordEncoder.encode("1234root"), AccessRole.ADMIN
        );

        create(newRootUser);
    }

    @SneakyThrows
    public UserEntry queryForUsername(final String username) {
        return super.getHandle().queryBuilder()
            .where()
            .eq("username", username)
            .queryForFirst();
    }

}
