package be.esi.prj.bookle.mvp.model.dataBaseRepository;

import java.util.List;

/**
 * Generic repository interface for managing entities.
 *
 * @param <K> the type of the key (e.g., ID)
 * @param <T> the type of the entity
 */
public interface Repository<K, T> {
    /**
     * Retrieves all entities.
     *
     * @return a list of all entities
     * @throws RepositoryException if a data access error occurs
     */
    List<T> findAll() throws RepositoryException;

    /**
     * Saves an entity.
     *
     * @param item the entity to save
     * @return the key of the saved entity
     * @throws RepositoryException if a data access error occurs
     */
    K save(T item) throws RepositoryException;
}

