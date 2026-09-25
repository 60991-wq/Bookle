package be.esi.prj.bookle.mvp.model.dataAccessObject;

import be.esi.prj.bookle.mvp.model.dataBaseRepository.RepositoryException;
import be.esi.prj.bookle.mvp.model.dto.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object (DAO) for the {@link Player} entity.
 * <p>
 * Allows interaction with the "players" table in the database.
 */
public class PlayerDao implements DAO<String, Player> {

    private final Connection connection;

    /**
     * Creates a new {@code PlayerDao} with the given database connection.
     *
     * @param connection the database connection (must not be null)
     */
    public PlayerDao(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "Connexion requise");
    }

    /**
     * Finds a player by name.
     *
     * @param name the player's name
     * @return an {@code Optional} containing the player if found, or empty if not
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public Optional<Player> findById(String name) throws RepositoryException {
        String sql = "SELECT * FROM players WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int score = rs.getInt("score");
                    return Optional.of(new Player(name, score));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur lors de findById", e);
        }
        return Optional.empty();
    }

    /**
     * Returns all players from the database.
     *
     * @return a list of all players
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public List<Player> findAll() throws RepositoryException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                players.add(new Player(name, score));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur lors de findAll", e);
        }
        return players;
    }

    /**
     * Saves a player to the database.
     * <p>
     * If the player already exists, it is updated.
     * Otherwise, a new player is inserted.
     *
     * @param player the player to save
     * @return the name of the saved player
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public String save(Player player) throws RepositoryException {
        try {
            boolean exists = findById(player.name()).isPresent();
            if (exists) {
                update(player);
            } else {
                insert(player);
            }
            return player.name();
        } catch (SQLException | RepositoryException e) {
            throw new RepositoryException("Erreur lors de save", e);
        }
    }

    /**
     * Inserts a new player into the database.
     *
     * @param player the player to insert
     * @throws SQLException if a database error occurs
     */
    private void insert(Player player) throws SQLException {
        String sql = "INSERT INTO players (name, score) VALUES (?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, player.name());
            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing player in the database.
     *
     * @param player the player to update
     * @throws SQLException if a database error occurs
     */
    private void update(Player player) throws SQLException {
        String sql = "UPDATE players SET score = ? WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, player.score());
            stmt.setString(2, player.name());
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a player by name.
     *
     * @param name the name of the player to delete
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public void deleteById(String name) throws RepositoryException {
        String sql = "DELETE FROM players WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Erreur lors de deleteById", e);
        }
    }
}
