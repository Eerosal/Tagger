package fi.eerosalla.web.tagger.controller.file;

import fi.eerosalla.web.tagger.config.FileConfig;
import fi.eerosalla.web.tagger.config.MinioConfig;
import fi.eerosalla.web.tagger.model.form.TagIdsForm;
import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.model.response.FileQueryResponse;
import fi.eerosalla.web.tagger.model.response.FileResponse;
import fi.eerosalla.web.tagger.repository.CombinedQueries;
import fi.eerosalla.web.tagger.repository.connection.ConnectionRepository;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.file.FileRepository;
import fi.eerosalla.web.tagger.repository.tag.TagEntry;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import fi.eerosalla.web.tagger.util.FileUtil;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Map;

@RolesAllowed("ADMIN")
@RestController
public class FileController {

    private final FileRepository fileRepository;
    private final ConnectionRepository connectionRepository;
    private final TagRepository tagRepository;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final FileConfig fileConfig;
    private final CombinedQueries combinedQueries;

    public FileController(final FileRepository fileRepository,
                          final ConnectionRepository connectionRepository,
                          final TagRepository tagRepository,
                          final MinioClient minioClient,
                          final MinioConfig minioConfig,
                          final FileConfig fileConfig,
                          final CombinedQueries combinedQueries) {
        this.fileRepository = fileRepository;
        this.connectionRepository = connectionRepository;
        this.tagRepository = tagRepository;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.fileConfig = fileConfig;
        this.combinedQueries = combinedQueries;
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
    public Object getFileDetails(
        final @ModelAttribute FileEntry file) {
        return getFileResponseWithTags(file);
    }

    @SneakyThrows
    @RequestMapping(
        value = "/api/files",
        method = RequestMethod.POST,
        consumes = "multipart/form-data"
    )
    public Object uploadFile(
        final @RequestParam(name = "filename") String filename,
        final @RequestParam(name = "file") MultipartFile multipartFile
    ) {
        if (filename == null || filename.isEmpty()
            || filename.length() > 255
            || multipartFile == null || multipartFile.isEmpty()
            || multipartFile.getOriginalFilename() == null) {
            return new ResponseEntity<>(
                HttpStatus.BAD_REQUEST
            );
        }

        if (multipartFile.getSize() > fileConfig.getMaxFileSize().toBytes()) {
            return new ResponseEntity<>(
                new ErrorResponse(
                    "File is too large (max "
                        + fileConfig.getMaxFileSize().toMegabytes()
                        + " MB)"
                ),
                HttpStatus.BAD_REQUEST
            );
        }

        String[] nameSplit = multipartFile.getOriginalFilename().split("\\.");
        String extension = nameSplit[nameSplit.length - 1].toLowerCase();
        FileType fileType = FileType.getByExtension(extension);
        if (fileType == null) {
            return new ResponseEntity<>(
                new ErrorResponse("Unknown file extension"),
                HttpStatus.BAD_REQUEST
            );
        }

        FileEntry file = new FileEntry();
        file.setName(filename);
        file.setExtension(fileType.getCanonicalExtension());

        fileRepository.create(file);

        File tempFile = FileUtil.getTempFile();
        try {
            multipartFile.transferTo(tempFile);

            FileUtil.upload(
                minioClient,
                minioConfig.getBucket(),
                tempFile,
                file.getInternalFilename(),
                fileType.getMimetype()
            );

            if (!fileType.equals(FileType.MP4)) {
                FileUtil.uploadThumbnail(
                    minioClient,
                    minioConfig.getBucket(),
                    tempFile,
                    file.getThumbnailFilename()
                );
            }
        } finally {
            tempFile.delete();
        }

        return getFileResponseWithTags(file);
    }

    @GetMapping("/api/files")
    public Object queryFiles(
        final @RequestParam @NotNull String query) {

        Map.Entry<Integer, List<FileEntry>> resultsEntry =
            combinedQueries.queryFiles(query);

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
