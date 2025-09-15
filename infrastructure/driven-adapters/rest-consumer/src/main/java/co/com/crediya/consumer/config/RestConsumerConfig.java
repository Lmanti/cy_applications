package co.com.crediya.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class RestConsumerConfig {

    public static final String CTX_AUTH_TOKEN = "AUTH_TOKEN";
    private final RestConsumerProperties restConsumerProperties;

    public RestConsumerConfig(RestConsumerProperties restConsumerProperties) {
        this.restConsumerProperties = restConsumerProperties;
    }

    @Bean
    public WebClient getWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl(restConsumerProperties.url())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .clientConnector(getClientHttpConnector())
            .filter(authPropagationFilter())
            .build();
    }

    private ClientHttpConnector getClientHttpConnector() {
        /*
        IF YO REQUIRE APPEND SSL CERTIFICATE SELF SIGNED: this should be in the default cacerts trustore
        */
        return new ReactorClientHttpConnector(HttpClient.create()
                .compress(true)
                .keepAlive(true)
                .option(CONNECT_TIMEOUT_MILLIS, restConsumerProperties.timeout())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(restConsumerProperties.timeout(), MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(restConsumerProperties.timeout(), MILLISECONDS));
                }));
    }

    @Bean
    public WebFilter authTokenContextFilter() {
        return (exchange, chain) -> {
            String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                return chain.filter(exchange).contextWrite(ctx -> ctx.put(CTX_AUTH_TOKEN, token));
            }
            return chain.filter(exchange);
        };
    }

    private ExchangeFilterFunction authPropagationFilter() {
        return (request, next) -> Mono.deferContextual(ctxView -> {
            String existing = request.headers().getFirst(HttpHeaders.AUTHORIZATION);
            if (existing != null && !existing.isBlank()) {
                return next.exchange(request);
            }
            
            String token = ctxView.hasKey(CTX_AUTH_TOKEN) ? ctxView.get(CTX_AUTH_TOKEN) : null;
            if (token == null || token.isBlank()) {
                return next.exchange(request);
            }
            
            return next.exchange(
                ClientRequest.from(request)
                    .headers(headers -> {
                        headers.setBearerAuth(token);
                    })
                    .build()
            );
        });
    }

}
