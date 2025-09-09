package co.com.crediya.api.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    @Test
    void securityHeadersFilterShouldAddHeaders() {
        // Arrange
        SecurityHeadersConfig securityHeadersConfig = new SecurityHeadersConfig();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/solicitud").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        WebFilterChain filterChain = mock(WebFilterChain.class);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());
        
        // Act
        Mono<Void> result = securityHeadersConfig.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .verifyComplete();
        
        HttpHeaders headers = exchange.getResponse().getHeaders();
        verify(filterChain).filter(exchange);
        
        assertEquals("default-src 'self'; frame-ancestors 'self'; form-action 'self'", 
                headers.getFirst("Content-Security-Policy"));
        assertEquals("max-age=31536000;", headers.getFirst("Strict-Transport-Security"));
        assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"));
        assertEquals("", headers.getFirst("Server"));
        assertEquals("no-store", headers.getFirst("Cache-Control"));
        assertEquals("no-cache", headers.getFirst("Pragma"));
        assertEquals("strict-origin-when-cross-origin", headers.getFirst("Referrer-Policy"));
    }

    @Test
    void corsConfigShouldCreateCorsWebFilter() {
        // Arrange
        CorsConfig corsConfig = new CorsConfig();
        String allowedOrigins = "http://localhost:4200,http://localhost:3000";
        
        // Act
        var corsWebFilter = corsConfig.corsWebFilter(allowedOrigins);
        
        // Assert
        assertNotNull(corsWebFilter, "CorsWebFilter should not be null");
    }
}