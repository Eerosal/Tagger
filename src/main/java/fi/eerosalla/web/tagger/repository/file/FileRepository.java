package fi.eerosalla.web.tagger.repository.file;

import com.j256.ormlite.support.ConnectionSource;
import fi.eerosalla.web.tagger.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public class FileRepository extends CrudRepository<FileEntry, Integer> {

    public FileRepository(final ConnectionSource connectionSource) {
        super(connectionSource, FileEntry.class);
    }

}
