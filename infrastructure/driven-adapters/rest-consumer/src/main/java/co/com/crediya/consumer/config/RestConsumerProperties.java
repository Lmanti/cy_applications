package co.com.crediya.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.restconsumer")
public record RestConsumerProperties(
    Integer timeout,
    String url
) {}