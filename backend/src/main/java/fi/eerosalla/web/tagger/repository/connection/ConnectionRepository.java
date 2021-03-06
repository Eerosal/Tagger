package fi.eerosalla.web.tagger.repository.connection;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
public class ConnectionRepository extends
    CrudRepository<ConnectionEntry, Integer> {


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
    public QueryBuilder<ConnectionEntry, Integer> getFileMatchQuery(
        final FileEntry fileEntry) {

        final var queryBuilder = getHandle().queryBuilder();

        queryBuilder.where()
            .eq("fileId", fileEntry.getId());

        return queryBuilder;
    }
}
