package com.nbs.nbsback.models.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class JsonConverter implements AttributeConverter<List<Object>, Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object convertToDatabaseColumn(List<Object> attribute) {
        try {
            // Convertir el array a un objeto JSON compatible con PostgreSQL
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<Object> convertToEntityAttribute(Object dbData) {
        try {
            if (dbData == null) {
                return List.of(); // Retornar lista vacía si los datos son nulos
            }
            // Convertir el objeto JSON de vuelta a una lista de objetos
            return objectMapper.readValue(dbData.toString(), new TypeReference<List<Object>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to list. Data: " + dbData, e);
        }
    }
}