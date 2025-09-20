package co.com.crediya.model.application.exception;

public class SQSMessagingException extends RuntimeException {

    public SQSMessagingException() {
        super("Failed to send message to SQS");
    }
    
    public SQSMessagingException(String message) {
        super(message);
    }
    
    public SQSMessagingException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
