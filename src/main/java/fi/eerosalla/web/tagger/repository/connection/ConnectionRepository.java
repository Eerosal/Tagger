package fi.eerosalla.web.tagger.repository.connection;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class ConnectionRepository extends
    CrudRepository<ConnectionEntry, Integer> {

    @Autowired
    private TagRepository tagRepository;

    public ConnectionRepository(final ConnectionSource connectionSource) {
        super(connectionSource, ConnectionEntry.class);
    }

    @SneakyThrows
    public void removeTagsFromFile(final int fileId,
                                   final List<Integer> tagIds) {
        final var deleteBuilder = getHandle().deleteBuilder();

        deleteBuilder.where()
            .eq("fileId", fileId)
            .and()
            .in("tagId", tagIds);

        deleteBuilder.delete();
    }

    @SneakyThrows
    public void addTagsToFile(final int fileId,
                              final List<Integer> tagIds) {
        List<ConnectionEntry> newConnections = tagIds.stream()
            .map(tagId -> {
                ConnectionEntry connectionEntry = new ConnectionEntry();

                connectionEntry.setFileId(fileId);
                connectionEntry.setTagId(tagId);

                return connectionEntry;
            }).collect(Collectors.toList());

        getHandle().create(newConnections);
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

    @SneakyThrows
    public QueryBuilder<ConnectionEntry, Integer> getFileMatchQuery(
        final FileEntry fileEntry) {

        final var queryBuilder = getHandle().queryBuilder();

        queryBuilder.where()
            .eq("fileId", fileEntry.getId());

        return queryBuilder;
    }
}
