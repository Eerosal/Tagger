package fi.eerosalla.web.tagger.repository;

import com.j256.ormlite.stmt.QueryBuilder;
import fi.eerosalla.web.tagger.repository.connection.ConnectionEntry;
import fi.eerosalla.web.tagger.repository.connection.ConnectionRepository;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.file.FileRepository;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CombinedQueries {

    private final FileRepository fileRepository;
    private final ConnectionRepository connectionRepository;
    private final TagRepository tagRepository;

    public CombinedQueries(final FileRepository fileRepository,
                           final ConnectionRepository connectionRepository,
                           final TagRepository tagRepository) {
        this.fileRepository = fileRepository;
        this.connectionRepository = connectionRepository;
        this.tagRepository = tagRepository;
    }

    @SneakyThrows
    public Map.Entry<Integer, List<FileEntry>> queryFiles(
        final String queryStr) {
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

        final var queryBuilder = fileRepository.getHandle().queryBuilder();

        int totalResultsCount;
        if (!tagNames.isEmpty()) {
            // TODO: used to use a join
            //  but idk how to get the total
            //  count of matches nicely with ormlite.
            //  it's good enough for now
            final var tagMatchQuery = getTagMatchQuery(tagNames);

            List<Integer> matchingFileIds = tagMatchQuery.query().stream()
                .map(ConnectionEntry::getFileId)
                .collect(Collectors.toList());

            queryBuilder.where()
                .in(
                    "id",
                    matchingFileIds
                );

            totalResultsCount = matchingFileIds.size();
        } else {
            totalResultsCount = (int) fileRepository.getHandle().countOf();
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
        if (page < 1) {
            return Map.entry(totalResultsCount, new ArrayList<>());
        }

        String pageSizeStr = modifiers.getOrDefault("page_size", "16");
        int pageSize = Integer.parseInt(pageSizeStr);
        if (pageSize < 1 || pageSize > 32) {
            return Map.entry(0, new ArrayList<>());
        }

        queryBuilder.offset(((long) (page - 1) * pageSize));
        queryBuilder.limit((long) pageSize);

        List<FileEntry> files = queryBuilder.query();
        if (files == null) {
            return Map.entry(0, new ArrayList<>());
        }

        return Map.entry(totalResultsCount, files);
    }

    @SneakyThrows
    public QueryBuilder<ConnectionEntry, Integer> getTagMatchQuery(
        final Collection<String> tagNames) {

        return connectionRepository.getHandle().queryBuilder()
            .selectColumns("fileId")
            .join(
                "tagId",
                "id",
                tagRepository.getNameQuery(tagNames)
            ).groupBy("fileId")
            .having("COUNT(1) = " + tagNames.size());
    }

}
