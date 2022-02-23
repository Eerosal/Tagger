package fi.eerosalla.web.tagger.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.tag.TagEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FileResponse {

    @JsonProperty("file")
    private FileEntry fileEntry;

    @JsonProperty("tags")
    private List<TagEntry> tagEntryList;

    public FileResponse(final FileEntry fileEntry) {
        this(fileEntry, new ArrayList<>());
    }

}
