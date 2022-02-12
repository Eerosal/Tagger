package fi.eerosalla.web.tagger.util;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

public class FileUtil {

    // used to avoid file name conflicts
    private static int tempFileCounter = 1;

    @SneakyThrows
    public static File getTempFile() {
        File tempFileDirectory = new File("/tmp/tagger/");
        if (!tempFileDirectory.exists()) {
            tempFileDirectory.mkdir();
        }

        File tempFile = new File(
            tempFileDirectory, tempFileCounter + ".temp"
        );

        tempFileCounter += 1;
        if (tempFileCounter == Integer.MAX_VALUE) {
            tempFileCounter = 1;
        }

        tempFile.deleteOnExit();

        return tempFile;
    }

    public static void uploadThumbnail(final MinioClient minioClient,
                                       final String bucket,
                                       final File originalFile,
                                       final String thumbnailFilename)
        throws Exception {
        File tempFile = getTempFile();
        createThumbnail(originalFile, tempFile, 150, 150);

        try {
            upload(
                minioClient,
                bucket,
                tempFile,
                thumbnailFilename,
                "image/jpeg"
            );
        } finally {
            tempFile.delete();
        }
    }

    public static void upload(final MinioClient minioClient,
                              final String bucket,
                              final File file,
                              final String filename,
                              final String mimetype) throws Exception {
        UploadObjectArgs uploadObjectArgs =
            MinioUtil.createUploadObjectArgs(
                bucket,
                filename,
                file.getCanonicalPath(),
                mimetype
            );

        minioClient.uploadObject(uploadObjectArgs);
    }

    public static void createThumbnail(final File srcFile,
                                       final File dstFile,
                                       final int maxWidth,
                                       final int maxHeight) throws Exception {
        Image originalImage =
            ImageIO.read(srcFile);

        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);

        if (width > maxWidth) {
            double scaleFactor = (double) maxWidth
                / (double) width;
            width = maxWidth;
            height = (int) (scaleFactor * height);
        }

        if (height > maxHeight) {
            double scaleFactor = (double) maxHeight
                / (double) height;
            height = maxHeight;
            width = (int) (scaleFactor * width);
        }

        BufferedImage scaledImage =
            new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB
            );

        scaledImage.createGraphics()
            .drawImage(
                originalImage.getScaledInstance(
                    width,
                    height,
                    BufferedImage.SCALE_SMOOTH
                ), 0, 0, null
            );

        ImageIO.write(
            scaledImage,
            "jpg",
            dstFile
        );
    }
}
