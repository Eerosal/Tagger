package fi.eerosalla.web.tagger.controller.files;

import fi.eerosalla.web.tagger.model.form.TagIdsForm;
import fi.eerosalla.web.tagger.model.response.FileQueryResponse;
import fi.eerosalla.web.tagger.model.response.FileResponse;
import fi.eerosalla.web.tagger.repository.connection.ConnectionRepository;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.file.FileRepository;
import fi.eerosalla.web.tagger.repository.tag.TagEntry;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
public class FileController {

    private final FileRepository fileRepository;

    private final ConnectionRepository connectionRepository;

    private final TagRepository tagRepository;

    public FileController(final FileRepository fileRepository,
                          final ConnectionRepository connectionRepository,
                          final TagRepository tagRepository) {
        this.fileRepository = fileRepository;
        this.connectionRepository = connectionRepository;
        this.tagRepository = tagRepository;
    }

    @SneakyThrows
    private FileResponse getFileResponseWithTags(final FileEntry file) {
        final var queryBuilder = tagRepository.getHandle().queryBuilder();

        queryBuilder.where()
            .in("id",
                connectionRepository.getFileMatchQuery(file)
                    .selectColumns("tagId")
            );

        List<TagEntry> tags = queryBuilder.query();

        return new FileResponse(
            file,
            tags
        );
    }

    @GetMapping("/api/files/{fileId}")
    public Object getFile(
        final @ModelAttribute FileEntry file) {
        return getFileResponseWithTags(file);
    }

    @RequestMapping(
        value = "/api/files",
        method = RequestMethod.POST,
        consumes = "multipart/form-data"
    )
    public Object uploadFile(
        final @RequestParam String filename) {
        //TODO: form
        FileEntry file = new FileEntry();
        file.setName(
            filename
        );

        return new FileResponse(
            fileRepository.create(file)
        );
    }

    @GetMapping("/api/files")
    public Object queryFiles(
        final @RequestParam @NotNull String query) {

        Map.Entry<Integer, List<FileEntry>> resultsEntry =
            fileRepository.query(query);

        return new FileQueryResponse(
            resultsEntry.getKey(),
            resultsEntry.getValue()
        );
    }

    @SneakyThrows
    @DeleteMapping("/api/files/{fileId}/remove-tags")
    public Object removeTags(
        final @ModelAttribute FileEntry file,
        final @RequestBody @Validated TagIdsForm tagsForm) {

        connectionRepository.removeTagsFromFile(
            file.getId(),
            tagsForm.getTagIds()
        );

        return getFileResponseWithTags(file);
    }

    @SneakyThrows
    @PostMapping("/api/files/{fileId}/add-tags")
    public Object addTags(
        final @ModelAttribute FileEntry file,
        final @RequestBody @Validated TagIdsForm tagsForm) {

        connectionRepository.addTagsToFile(
            file.getId(),
            tagsForm.getTagIds()
        );

        return getFileResponseWithTags(file);
    }

}
