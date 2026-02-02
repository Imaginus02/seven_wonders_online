package com.reynaud.wonders.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reynaud.wonders.model.Science;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.EnumMap;
import java.util.Map;

@Converter
public class ScienceConverter implements AttributeConverter<Map<Science, Integer>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Science, Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize science map", e);
        }
    }

    @Override
    public Map<Science, Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new EnumMap<>(Science.class);
        }
        try {
            Map<Science, Integer> map = OBJECT_MAPPER.readValue(dbData, new TypeReference<Map<Science, Integer>>() {
            });
            if (map == null || map.isEmpty()) {
                return new EnumMap<>(Science.class);
            }
            return new EnumMap<>(map);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to deserialize science map", e);
        }
    }
}
