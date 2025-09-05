package co.com.crediya.model.application.exception;

public class DataRetrievalException extends RuntimeException {
    public DataRetrievalException() {
        super("Error while retrieving data");
    }
    
    public DataRetrievalException(String message) {
        super(message);
    }
    
    public DataRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
