package fi.eerosalla.web.tagger.model.response;

import fi.eerosalla.web.tagger.repository.file.FileEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FileQueryResponse {

    private int totalResultsCount;

    private List<FileEntry> results;

}
