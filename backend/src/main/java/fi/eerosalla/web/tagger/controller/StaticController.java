package fi.eerosalla.web.tagger.controller;

import fi.eerosalla.web.tagger.config.MinioConfig;
import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.util.MinioUtil;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@RolesAllowed("ADMIN")
@RestController
public class StaticController {

    private final MinioClient minioClient;

    private final RestTemplate restTemplate = new RestTemplate();
    private final MinioConfig minioConfig;

    public StaticController(final MinioClient minioClient,
                            final MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    private static final Pattern FILENAME_PATTERN =
        Pattern.compile("^[a-z0-9_]+\\.[a-z0-9]{3}$", Pattern.CASE_INSENSITIVE);

    @GetMapping("/static/**")
    public ResponseEntity<?> minioMirror(final HttpServletRequest request)
        throws Exception {
        String originalUriStr = request.getRequestURI();
        int staticIndex = originalUriStr.indexOf("/static/");

        String objectName = originalUriStr.substring(
            staticIndex + "/static/".length()
        );

        if (objectName.length() > 64
            || !FILENAME_PATTERN.matcher(objectName).matches()) {
            return new ResponseEntity<>(
                new ErrorResponse("Invalid filename"),
                HttpStatus.BAD_REQUEST
            );
        }

        // the url isn't visible to users so the lifetime doesn't really matter
        // (unless something unexpected like an error occurs)
        GetPresignedObjectUrlArgs args =
            MinioUtil.createGetPresignedObjectUrlArgs(
                minioConfig.getBucket(), objectName, 120
            );
        String presignedUrl = minioClient.getPresignedObjectUrl(args);

        URI uri = new URI(presignedUrl);

        return restTemplate.exchange(uri, HttpMethod.GET, null, byte[].class);
    }

}
