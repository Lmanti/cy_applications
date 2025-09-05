package co.com.crediya.model.application.exception;

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
