package fi.eerosalla.web.tagger.controller.files;

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
import io.minio.UploadObjectArgs;
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

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
public class FileController {

    //10MB
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1000L * 1000L;

    private static final Map<String, String> KNOWN_EXTENSIONS =
        new HashMap<>();

    static {
        KNOWN_EXTENSIONS.put("png", "image/png");
        KNOWN_EXTENSIONS.put("jpg", "image/jpeg");
        KNOWN_EXTENSIONS.put("gif", "image/gif");
        KNOWN_EXTENSIONS.put("mp4", "video/mp4");
    }

    private final FileRepository fileRepository;

    private final ConnectionRepository connectionRepository;

    private final TagRepository tagRepository;

    private final MinioClient minioClient;

    public FileController(final FileRepository fileRepository,
                          final ConnectionRepository connectionRepository,
                          final TagRepository tagRepository,
                          final MinioClient minioClient) {
        this.fileRepository = fileRepository;
        this.connectionRepository = connectionRepository;
        this.tagRepository = tagRepository;
        this.minioClient = minioClient;
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

    @SneakyThrows
    @RequestMapping(
        value = "/api/files",
        method = RequestMethod.POST,
        consumes = "multipart/form-data"
    )
    public Object uploadFile(
        final @RequestParam(name = "filename") String filename,
        final @RequestParam(name = "file") MultipartFile multipartFile) {

        if (filename == null || filename.isEmpty()) {
            return new ResponseEntity<>(
                new ErrorResponse("Missing filename"),
                HttpStatus.BAD_REQUEST
            );
        }

        if (multipartFile == null) {
            return new ResponseEntity<>(
                new ErrorResponse("Missing file"),
                HttpStatus.BAD_REQUEST
            );
        }

        if (multipartFile.isEmpty()
            || multipartFile.getSize() == 0) {
            return new ResponseEntity<>(
                new ErrorResponse("File cannot be empty"),
                HttpStatus.BAD_REQUEST
            );
        }

        if (multipartFile.getSize() > MAX_FILE_SIZE_BYTES) {
            return new ResponseEntity<>(
                new ErrorResponse(
                    "File is too large (max "
                        + MAX_FILE_SIZE_BYTES
                        + " bytes)"
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
        file.setName(
            filename
        );
        file.setExtension(
            extension
        );

        FileEntry fileWithId = fileRepository.create(file);

        String internalFilename = fileWithId.getId() + "." + extension;
        String thumbnailFilename = fileWithId.getId() + "_thumbnail.jpg";

        // TODO: btw, this overwrites files
        FileUtil.getTempFile(internalFilename,
            new Consumer<>() {
                @SneakyThrows
                @Override
                public void accept(final File tempFile) {
                    multipartFile.transferTo(tempFile);

                    // TODO: bork for mp4
                    if(!extension.equals("mp4")) {
                        FileUtil.getTempFile(thumbnailFilename,
                            new Consumer<>() {
                                @SneakyThrows
                                @Override
                                public void accept(final File tempFile2) {
                                    Image originalImage = ImageIO.read(tempFile);

                                    int width = originalImage.getWidth(null);
                                    int height = originalImage.getHeight(null);

                                    if (width > 150) {
                                        double scaleFactor = 150.0
                                            / (double) width;
                                        width = 150;
                                        height = (int) (scaleFactor * height);
                                    }

                                    if (height > 150) {
                                        double scaleFactor = 150.0
                                            / (double) height;
                                        height = 150;
                                        width = (int) (scaleFactor * width);
                                    }

                                    BufferedImage scaledImage = new BufferedImage(
                                        width, height, BufferedImage.TYPE_INT_RGB
                                    );

                                    scaledImage.createGraphics()
                                        .drawImage(
                                            originalImage.getScaledInstance(
                                                width,
                                                height,
                                                BufferedImage.SCALE_SMOOTH
                                            ), 0, 0, null
                                        );

                                    ImageIO.write(scaledImage, "jpg", tempFile2);

                                    UploadObjectArgs.Builder uploadBuilder =
                                        UploadObjectArgs.builder();

                                    uploadBuilder.bucket("tg-thumbnails");
                                    uploadBuilder.object(thumbnailFilename);
                                    uploadBuilder.filename(
                                        tempFile2.getCanonicalPath()
                                    );
                                    uploadBuilder.contentType(mimetype);

                                    minioClient.uploadObject(uploadBuilder.build());
                                }
                            }
                        );
                    }

                    UploadObjectArgs.Builder uploadBuilder =
                        UploadObjectArgs.builder();

                    uploadBuilder.bucket("tg-files");
                    uploadBuilder.object(internalFilename);
                    uploadBuilder.filename(tempFile.getCanonicalPath());
                    uploadBuilder.contentType(mimetype);

                    minioClient.uploadObject(uploadBuilder.build());
                }
            }
        );


        return new FileResponse(
            fileWithId
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
