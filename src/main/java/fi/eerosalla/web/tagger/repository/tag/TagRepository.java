package fi.eerosalla.web.tagger.repository.tag;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TagRepository extends CrudRepository<TagEntry, Integer> {

    public TagRepository(final ConnectionSource connectionSource) {
        super(connectionSource, TagEntry.class);
    }

    @SneakyThrows
    public QueryBuilder<TagEntry, Integer> getNameQuery(
            final Collection<String> tagNames) {
        final var queryBuilder = getHandle().queryBuilder();

        queryBuilder.where()
                .in("name", tagNames);

        return queryBuilder;
    }

}
