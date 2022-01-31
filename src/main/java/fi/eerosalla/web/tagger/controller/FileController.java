package fi.eerosalla.web.tagger.controller;

import fi.eerosalla.web.tagger.repository.connection.ConnectionEntry;
import fi.eerosalla.web.tagger.repository.connection.ConnectionRepository;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.file.FileRepository;
import fi.eerosalla.web.tagger.repository.tag.TagEntry;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    private final Integer kissaId;
    private final Integer koiraId;
    private final Integer kalaId;
    private final FileRepository fileRepository;

    @PostMapping("/api/files")
    public Object uploadFile() {
        return "asd";
    }

    private final TagRepository tagRepository;

    private final ConnectionRepository connectionRepository;

    public FileController(final FileRepository fileRepository,
                          final TagRepository tagRepository,
                          final ConnectionRepository connectionRepository) {
        TagEntry kissa = new TagEntry();
        kissa.setName("kissa");

        TagEntry koira = new TagEntry();
        koira.setName("koira");

        TagEntry kala = new TagEntry();
        kala.setName("kala");

        kissaId = tagRepository.create(kissa).getId();
        koiraId = tagRepository.create(koira).getId();
        kalaId = tagRepository.create(kala).getId();
        this.fileRepository = fileRepository;
        this.tagRepository = tagRepository;
        this.connectionRepository = connectionRepository;
    }

    @GetMapping("/debug/addFile")
    public Object addDebugFile() {

        for (int i = 1; i <= 500; ++i) {
            FileEntry fileEntry = new FileEntry();
            fileEntry.setName("testi");

            int fileId = fileRepository.create(fileEntry).getId();

            if (i % 3 == 0) {
                ConnectionEntry entry = new ConnectionEntry();
                entry.setFileId(fileId);
                entry.setTagId(kissaId);
                connectionRepository.create(entry);
            }

            if (i % 5 == 0) {
                ConnectionEntry entry = new ConnectionEntry();
                entry.setFileId(fileId);
                entry.setTagId(koiraId);
                connectionRepository.create(entry);
            }

            if (i % 2 == 0) {
                ConnectionEntry entry = new ConnectionEntry();
                entry.setFileId(fileId);
                entry.setTagId(kalaId);
                connectionRepository.create(entry);
            }
        }

        return "OK";
    }

    @GetMapping("/api/files")
    public Object getFiles(
        final @RequestParam(name = "query") String queryStr) {
        return fileRepository.query(queryStr);
    }
}
