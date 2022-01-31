package fi.eerosalla.web.tagger.repository;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;

public abstract class CrudRepository<T, ID> {

    @Getter
    private final Dao<T, ID> handle;

    @SneakyThrows
    public CrudRepository(final ConnectionSource connectionSource,
                          final Class<T> clazz) {
        this.handle = DaoManager.createDao(
                connectionSource,
                clazz
        );

        try {
            TableUtils.createTableIfNotExists(
                    connectionSource,
                    clazz
            );
        } catch (Exception ignored) {
        }
    }

    @SneakyThrows
    public List<T> queryForAll() {
        return handle.queryForAll();
    }

    @SneakyThrows
    public T create(final T value) {
        handle.create(value);

        return value;
    }

    @SneakyThrows
    public T queryForId(final ID id) {
        return handle.queryForId(id);
    }

    @SneakyThrows
    public void update(final T value) {
        handle.createOrUpdate(value);
    }

    @SneakyThrows
    public void deleteById(final ID id) {
        handle.deleteById(id);
    }

}
