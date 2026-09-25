package be.esi.prj.bookle.mvp.model.dataBaseRepository;

/**
 * Exception thrown when a data access or repository operation fails.
 */
public class RepositoryException extends Exception {

    /**
     * Creates a new {@code RepositoryException} with no detail message.
     */
    public RepositoryException() {
        super();
    }

    /**
     * Creates a new {@code RepositoryException} with the specified detail message.
     *
     * @param message the detail message
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Creates a new {@code RepositoryException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
