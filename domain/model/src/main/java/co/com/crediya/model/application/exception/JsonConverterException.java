package co.com.crediya.model.application.exception;

public class JsonConverterException extends RuntimeException {
    public JsonConverterException() {
        super("Error in JsonConverterException");
    }
    
    public JsonConverterException(String message) {
        super(message);
    }
    
    public JsonConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
