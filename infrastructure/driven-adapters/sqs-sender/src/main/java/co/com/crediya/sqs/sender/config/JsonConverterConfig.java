package co.com.crediya.sqs.sender.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import co.com.crediya.sqs.sender.utility.JsonConverter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConverterConfig {

    @Bean
    public ObjectMapper jsonObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    @Bean
    public JsonConverter jsonConverter(@Qualifier("jsonObjectMapper") ObjectMapper objectMapper) {
        return new JsonConverter(objectMapper);
    }
}
