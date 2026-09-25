package be.esi.prj.bookle.mvp.model.dataAccessObject;

import be.esi.prj.bookle.mvp.model.dataBaseRepository.RepositoryException;
import be.esi.prj.bookle.mvp.model.dto.WordKey;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object (DAO) implementation for the {@link WordKey} entity.
 * Provides methods to interact with the "words" table in the database.
 */
public class WordKeyDao implements DAO<WordKey, WordKey> {

    private final Connection connection;

    /**
     * Constructs a {@code WordKeyDao} with the given database connection.
     *
     * @param connection the database connection (must not be null)
     */
    public WordKeyDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connexion requise");
    }

    /**
     * Finds a {@link WordKey} by its full key (word, book title, and page).
     *
     * @param key the key to search for
     * @return an {@code Optional} containing the key if found, otherwise empty
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<WordKey> findById(WordKey key) throws RepositoryException {
        String sql = """
                    SELECT * FROM words
                    WHERE word = ? AND titre_livre = ? AND numero_page = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, key.word());
            stmt.setString(2, key.bookTitle());
            stmt.setInt(3, key.page());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(key); // if the key is known, we return it
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur findById WordKey", e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all {@link WordKey} entries from the database.
     *
     * @return a list of all word keys
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public List<WordKey> findAll() throws RepositoryException {
        List<WordKey> keys = new ArrayList<>();
        String sql = "SELECT word, titre_livre, numero_page FROM words";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                keys.add(new WordKey(rs.getString("word"), rs.getString("titre_livre"), rs.getInt("numero_page")));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Erreur findAll WordKey", e);
        }
        return keys;
    }

    /**
     * Saves a {@link WordKey} to the database if it does not already exist.
     *
     * @param key the word key to save
     * @return the saved word key
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public WordKey save(WordKey key) throws RepositoryException {
        if (findById(key).isEmpty()) {
            String sql = """
                        INSERT INTO words (word, titre_livre, numero_page)
                        VALUES (?, ?, ?)
                    """;
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, key.word());
                stmt.setString(2, key.bookTitle());
                stmt.setInt(3, key.page());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Erreur save WordKey", e);
            }
        }
        return key;
    }

    /**
     * Deletes a {@link WordKey} from the database by its key.
     *
     * @param key the word key to delete
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public void deleteById(WordKey key) throws RepositoryException {
        String sql = """
                    DELETE FROM words
                    WHERE word = ? AND titre_livre = ? AND numero_page = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, key.word());
            stmt.setString(2, key.bookTitle());
            stmt.setInt(3, key.page());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erreur deleteById WordKey", e);
        }
    }

    /**
     * Finds the most frequent 5-letter word on a specific page of a book.
     *
     * @param title the title of the book
     * @param page  the page number
     * @return the most frequent 5-letter word, or {@code null} if none found
     * @throws Exception if a database error occurs
     */
    public String findMostFrequentWord(String title, int page) throws Exception {
        String sql = "SELECT word FROM words WHERE titre_livre = ? AND numero_page = ? AND LENGTH(word) = 5 " + "GROUP BY word ORDER BY COUNT(*) DESC LIMIT 1";


        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setInt(2, page);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("word");
            }
        }
        return null;
    }

    /**
     * Finds all {@link WordKey} entries matching a given title and optional page.
     *
     * @param title the book title to search for
     * @param page  optional page number filter
     * @return a list of matching word keys
     * @throws RepositoryException if a database error occurs
     */
    public List<WordKey> findByTitleAndPage(String title, Optional<Integer> page) throws RepositoryException {
        List<WordKey> results = new ArrayList<>();
        String sql = "SELECT word, titre_livre, numero_page FROM words WHERE LOWER(titre_livre) LIKE ?" + (page.isPresent() ? " AND numero_page = ?" : "");

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + title.toLowerCase() + "%");
            if (page.isPresent()) {
                stmt.setInt(2, page.get());
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new WordKey(rs.getString("word"), rs.getString("titre_livre"), rs.getInt("numero_page")));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur findByTitleAndPage WordKey", e);
        }
        return results;
    }
}
