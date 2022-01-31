package fi.eerosalla.web.tagger.repository.connection;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;


@Repository
public class ConnectionRepository extends
    CrudRepository<ConnectionEntry, Integer> {

    @Autowired
    private TagRepository tagRepository;

    public ConnectionRepository(final ConnectionSource connectionSource) {
        super(connectionSource, ConnectionEntry.class);
    }

    @SneakyThrows
    public QueryBuilder<ConnectionEntry, Integer> getTagMatchQuery(
        final Collection<String> tagNames) {

        return getHandle().queryBuilder()
            .selectColumns("fileId")
            .join(
                "tagId",
                "id",
                tagRepository.getNameQuery(tagNames)
            ).groupBy("fileId")
            .having("COUNT(1) = " + tagNames.size());
    }
}
