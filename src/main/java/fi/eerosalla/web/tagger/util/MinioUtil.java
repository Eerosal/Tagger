package fi.eerosalla.web.tagger.util;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.UploadObjectArgs;
import io.minio.http.Method;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;


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

    public static GetObjectArgs createGetObjectArgs(
        final String bucket,
        final String object
    ) {
        GetObjectArgs.Builder getBuilder =
            GetObjectArgs.builder();

        getBuilder.bucket(bucket);
        getBuilder.object(object);

        return getBuilder.build();
    }

    public static GetPresignedObjectUrlArgs createGetPresignedObjectUrlArgs(
        final String bucket,
        final String object
    ) {
        GetPresignedObjectUrlArgs.Builder getBuilder =
            GetPresignedObjectUrlArgs.builder();

        getBuilder.bucket(bucket);
        getBuilder.object(object);
        getBuilder.method(Method.GET);
        getBuilder.expiry(10, TimeUnit.MINUTES);

        return getBuilder.build();
    }
}
