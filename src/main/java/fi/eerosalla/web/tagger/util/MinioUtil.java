package fi.eerosalla.web.tagger.util;

import io.minio.UploadObjectArgs;
import lombok.SneakyThrows;

public class MinioUtil {

    @SneakyThrows
    public static UploadObjectArgs createUploadObjectArgs(
        final String bucket,
        final String object,
        final String filename,
        final String contentType
    ) {
        UploadObjectArgs.Builder uploadBuilder =
            UploadObjectArgs.builder();

        uploadBuilder.bucket(bucket);
        uploadBuilder.object(object);
        uploadBuilder.filename(filename);
        uploadBuilder.contentType(contentType);

        return uploadBuilder.build();
    }

}
