package be.esi.prj.bookle.mvp.model.dataBaseRepository;

import be.esi.prj.bookle.mvp.model.dataAccessObject.PlayerDao;
import be.esi.prj.bookle.mvp.model.dto.Player;

import java.util.List;

/**
 * Repository implementation for {@link Player} entities.
 * <p>
 * Uses a {@link PlayerDao} to perform database operations.
 */
public class PlayerRepository implements Repository<String, Player> {

    private final PlayerDao playerDao;

    /**
     * Constructs a {@code PlayerRepository} with the given {@link PlayerDao}.
     *
     * @param playerDao the data access object for players
     */
    public PlayerRepository(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    /**
     * Retrieves all players.
     *
     * @return a list of all players
     * @throws RepositoryException if a data access error occurs
     */
    @Override
    public List<Player> findAll() throws RepositoryException {
        return playerDao.findAll();
    }

    /**
     * Saves a player to the database.
     *
     * @param player the player to save
     * @return the name of the saved player
     * @throws RepositoryException if a data access error occurs
     */
    @Override
    public String save(Player player) throws RepositoryException {
        return playerDao.save(player);
    }
}
