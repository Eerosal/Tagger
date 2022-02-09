package fi.eerosalla.web.tagger.util;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

public class FileUtil {

    @SneakyThrows
    public static void getTempFile(final String filename,
                                   final Consumer<File> tempFileConsumer) {
        File tempFileDirectory = new File("/tmp/tagger/");
        if (!tempFileDirectory.exists()) {
            tempFileDirectory.mkdir();
        }

        if (filename == null || filename.isEmpty()) {
            return;
        }

        File tempFile = new File(tempFileDirectory, filename);
        tempFile.deleteOnExit();

        tempFileConsumer.accept(tempFile);

        System.out.println("DELETE " + tempFile.getCanonicalPath());
    }

    @SneakyThrows
    public static void createThumbnail(final File srcFile,
                                       final File dstFile,
                                       final int maxWidth,
                                       final int maxHeight) {
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
