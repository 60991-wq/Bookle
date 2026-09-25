package be.esi.prj.bookle.mvp.model.dataAccessObject;

import be.esi.prj.bookle.mvp.model.dataBaseRepository.RepositoryException;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface for Data Access Objects (DAO).
 * <p>
 * Provides basic methods to interact with a data source.
 *
 * @param <K> the type of the key (e.g., ID)
 * @param <T> the type of the data object
 */
public interface DAO<K, T> {
    /**
     * Finds an item by its key.
     *
     * @param key the key of the item
     * @return an {@code Optional} containing the item if found, or empty if not found
     * @throws RepositoryException if a data access error occurs
     */
    Optional<T> findById(K key) throws RepositoryException;

    /**
     * Returns all items.
     *
     * @return a list of all items
     * @throws RepositoryException if a data access error occurs
     */
    List<T> findAll() throws RepositoryException;

    /**
     * Saves an item.
     *
     * @param item the item to save
     * @return the key of the saved item
     * @throws RepositoryException if a data access error occurs
     */
    K save(T item) throws RepositoryException;

    /**
     * Deletes an item by its key.
     *
     * @param key the key of the item to delete
     * @throws RepositoryException if a data access error occurs
     */
    void deleteById(K key) throws RepositoryException;
}
