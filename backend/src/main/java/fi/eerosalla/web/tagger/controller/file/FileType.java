package fi.eerosalla.web.tagger.controller.file;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FileType {

    PNG("image/png", "png"),
    GIF("image/gif", "gif"),
    JPEG("image/jpeg", "jpg", "jpeg"),
    MP4("video/mp4", "mp4");

    private static Map<String, FileType> extensionMap = new HashMap<>();

    @Getter
    private final String mimetype;

    @Getter
    private final String[] extensions;

    public static FileType getByExtension(final String extension) {
        return extensionMap.get(extension);
    }

    public String getCanonicalExtension() {
        return extensions[0];
    }

    FileType(final String mimetype, final String... extensions) {
        Assert.notEmpty(extensions, "Invalid file enum value");

        this.mimetype = mimetype;
        this.extensions = extensions;
    }

    static {
        for (FileType fileType : EnumSet.allOf(FileType.class)) {
            for (String extension : fileType.getExtensions()) {
                extensionMap.put(extension, fileType);
            }
        }
    }

}
