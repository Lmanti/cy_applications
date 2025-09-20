package co.com.crediya.sqs.sender.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.crediya.model.application.exception.JsonConverterException;

public class JsonConverter {
    private final ObjectMapper objectMapper;
    
    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonConverterException("Error al convertir objeto a JSON", e);
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonConverterException("Error al convertir JSON a objeto", e);
        }
    }
}
