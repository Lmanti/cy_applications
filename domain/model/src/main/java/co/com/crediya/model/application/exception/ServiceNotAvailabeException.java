package co.com.crediya.model.application.exception;

public class ServiceNotAvailabeException extends RuntimeException {
    public ServiceNotAvailabeException() {
        super("Service not available");
    }
    
    public ServiceNotAvailabeException(String message) {
        super(message);
    }
    
    public ServiceNotAvailabeException(String message, Throwable cause) {
        super(message, cause);
    }
}
