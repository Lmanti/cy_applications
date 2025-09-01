package co.com.crediya.consumer.exception;

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
