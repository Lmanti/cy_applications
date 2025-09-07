package co.com.crediya.consumer.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
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
    private final String url;
    private final int timeout;

    public RestConsumerConfig(@Value("${adapter.restconsumer.url}") String url,
                              @Value("${adapter.restconsumer.timeout}") int timeout) {
        this.url = url;
        this.timeout = timeout;
    }

    @Bean
    public WebClient getWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl(url)
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
                .option(CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeout, MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeout, MILLISECONDS));
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
            if (token != null && !token.isBlank()) {
                ClientRequest newReq = ClientRequest.from(request)
                    .headers(h -> h.setBearerAuth(token))
                    .build();
                return next.exchange(newReq);
            }
            return next.exchange(request);
        });
    }

}
