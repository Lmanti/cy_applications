package co.com.crediya.model.application.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException() {
        super("Invalid data");
    }
    
    public InvalidDataException(String message) {
        super(message);
    }
    
    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
