package fi.eerosalla.web.tagger.util;

import lombok.SneakyThrows;

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

}
