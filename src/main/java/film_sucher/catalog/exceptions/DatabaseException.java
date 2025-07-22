package film_sucher.catalog.exceptions;

public class DatabaseException extends RuntimeException {
     public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
