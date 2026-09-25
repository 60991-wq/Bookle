package be.esi.prj.bookle.mvp.model.dataAccessObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class provides access to the database connection.
 * <p>
 * It uses a single (shared) connection for the whole application.
 */
public class ConnectionManager {
    private static Connection connection;

    /**
     * Returns the database connection.
     * <p>
     * If the connection does not exist yet, it is created.
     * Otherwise, the existing connection is returned.
     *
     * @return the database connection
     * @throws RuntimeException if a database connection error occurs
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:sqlite:external-data/bookle.db";
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);


            } catch (SQLException ex) {
                throw new RuntimeException("Connexion impossible", ex);
            }
        }
        return connection;
    }
}

