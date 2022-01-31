package fi.eerosalla.web.tagger.repository.file;

import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import fi.eerosalla.web.tagger.repository.connection.ConnectionRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class FileRepository extends CrudRepository<FileEntry, Integer> {

    @Autowired
    private ConnectionRepository connectionRepository;

    public FileRepository(final ConnectionSource connectionSource) {
        super(connectionSource, FileEntry.class);
    }

    @SneakyThrows
    public List<FileEntry> query(final String queryStr) {
        HashSet<String> tagNames = new HashSet<>();
        Map<String, String> modifiers = new HashMap<>();

        if (!queryStr.isEmpty()) {
            String[] parts = queryStr.split(" ");

            for (String part : parts) {
                String[] partSplit =
                    part.toLowerCase().split(":", 2);

                if (partSplit.length == 1) {
                    tagNames.add(partSplit[0]);
                } else {
                    modifiers.put(partSplit[0], partSplit[1]);
                }
            }
        }

        final var queryBuilder = getHandle().queryBuilder();

        if (!tagNames.isEmpty()) {
            queryBuilder.where()
                .in(
                    "id",
                    connectionRepository.getTagMatchQuery(tagNames)
                );
        }

        String orderStr = modifiers.getOrDefault("order", "id_desc");
        switch (orderStr) {
            default:
            case "id":
            case "id_desc":
                boolean ascending = !orderStr.endsWith("_desc");

                queryBuilder.orderBy(
                    "id",
                    ascending
                );
        }

        String pageStr = modifiers.getOrDefault("page", "1");
        int page = Integer.parseInt(pageStr);
        if (page < 1 || page > 256) {
            return new ArrayList<>();
        }

        String pageSizeStr = modifiers.getOrDefault("page_size", "16");
        int pageSize = Integer.parseInt(pageSizeStr);
        if (pageSize < 1 || pageSize > 32) {
            return new ArrayList<>();
        }

        queryBuilder.offset((long) ((page - 1) * pageSize));
        queryBuilder.limit((long) pageSize);

        return queryBuilder.query();
    }

}
