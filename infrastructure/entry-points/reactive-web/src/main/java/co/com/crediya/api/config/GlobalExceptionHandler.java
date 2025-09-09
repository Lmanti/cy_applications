package co.com.crediya.api.config;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.model.application.exception.DataPersistenceException;
import co.com.crediya.model.application.exception.InvalidDataException;
import co.com.crediya.model.application.exception.ServiceNotAvailabeException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidDataException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidDataException(
            InvalidDataException ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, ex.getMessage(), HttpStatus.BAD_REQUEST, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
    }

    @ExceptionHandler(ServiceNotAvailabeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleServiceNotAvailabeException(
            ServiceNotAvailabeException ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, ex.getMessage(), HttpStatus.FORBIDDEN, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse));
    }
    
    @ExceptionHandler(DataPersistenceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataPersistenceException(
            DataPersistenceException ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, "Error en la persistencia de datos: " + ex.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        
        String path = exchange.getRequest().getPath().value();
        String traceId = exchange.getRequest().getId();
        
        ErrorResponse errorResponse = buildErrorResponse(
                path, "Error interno del servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, traceId);
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    private ErrorResponse buildErrorResponse(String path, String message, 
                                            HttpStatus status, String traceId) {
        return ErrorResponse.builder()
                .path(path)
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
    }
}
