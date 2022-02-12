package fi.eerosalla.web.tagger.controller;

import fi.eerosalla.web.tagger.config.FileConfig;
import fi.eerosalla.web.tagger.config.MinioConfig;
import fi.eerosalla.web.tagger.model.form.TagIdsForm;
import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.model.response.FileQueryResponse;
import fi.eerosalla.web.tagger.model.response.FileResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RolesAllowed("ADMIN")
@RestController
public class FileController {


    private static final Map<String, String> KNOWN_EXTENSIONS =
        new HashMap<>();

    // TODO: enum
    static {
        KNOWN_EXTENSIONS.put("png", "image/png");
        KNOWN_EXTENSIONS.put("jpg", "image/jpeg");
        KNOWN_EXTENSIONS.put("gif", "image/gif");
        KNOWN_EXTENSIONS.put("mp4", "video/mp4");
        // TODO: more formats
    }

    private final FileRepository fileRepository;

    private final ConnectionRepository connectionRepository;

    private final TagRepository tagRepository;

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final FileConfig fileConfig;

    public FileController(final FileRepository fileRepository,
                          final ConnectionRepository connectionRepository,
                          final TagRepository tagRepository,
                          final MinioClient minioClient,
                          final MinioConfig minioConfig,
                          final FileConfig fileConfig) {
        this.fileRepository = fileRepository;
        this.connectionRepository = connectionRepository;
        this.tagRepository = tagRepository;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.fileConfig = fileConfig;
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

    // TODO: fetch => axios
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
        String mimetype = KNOWN_EXTENSIONS.get(extension);
        if (mimetype == null) {
            return new ResponseEntity<>(
                new ErrorResponse("Unknown file extension"),
                HttpStatus.BAD_REQUEST
            );
        }

        FileEntry file = new FileEntry();
        file.setName(filename);
        file.setExtension(extension);

        FileEntry fileWithId = fileRepository.create(file);

        String internalFilename = fileWithId.getId() + "." + extension;

        File tempFile = FileUtil.getTempFile();
        try {
            multipartFile.transferTo(tempFile);

            FileUtil.upload(
                minioClient,
                minioConfig.getBucket(),
                tempFile,
                internalFilename,
                mimetype
            );

            // TODO: more formats support, not future proof
            if (!mimetype.equals("video/mp4")) {
                FileUtil.uploadThumbnail(
                    minioClient,
                    minioConfig.getBucket(),
                    tempFile,
                    fileWithId.getId()
                );
            }
        } finally {
            tempFile.delete();
        }

        return getFileResponseWithTags(fileWithId);
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
