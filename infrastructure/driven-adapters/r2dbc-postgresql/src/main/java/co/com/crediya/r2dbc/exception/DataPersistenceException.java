package co.com.crediya.r2dbc.exception;

public class DataPersistenceException extends RuntimeException {
    public DataPersistenceException() {
        super("Error trying to save data");
    }
    
    public DataPersistenceException(String message) {
        super(message);
    }
    
    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
