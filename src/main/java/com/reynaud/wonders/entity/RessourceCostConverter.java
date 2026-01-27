package com.reynaud.wonders.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.EnumMap;
import java.util.Map;

@Converter
public class RessourceCostConverter implements AttributeConverter<Map<Ressources, Integer>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Ressources, Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize card cost", e);
        }
    }

    @Override
    public Map<Ressources, Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new EnumMap<>(Ressources.class);
        }
        try {
            Map<Ressources, Integer> map = OBJECT_MAPPER.readValue(dbData, new TypeReference<Map<Ressources, Integer>>() {
            });
            if (map == null) {
                return new EnumMap<>(Ressources.class);
            }
            return new EnumMap<>(map);
        } catch (JsonProcessingException e) {
            return parseLegacyFormat(dbData);
        }
    }

    private Map<Ressources, Integer> parseLegacyFormat(String raw) {
        EnumMap<Ressources, Integer> result = new EnumMap<>(Ressources.class);
        String content = raw.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1);
        }
        if (content.isBlank()) {
            return result;
        }

        String[] entries = content.split(",");
        for (String entry : entries) {
            String[] parts = entry.split(":");
            if (parts.length != 2) {
                continue;
            }

            Ressources resource = toRessource(parts[0]);
            if (resource == null) {
                continue;
            }

            Integer quantity = parseQuantity(parts[1]);
            if (quantity == null) {
                continue;
            }

            result.put(resource, quantity);
        }
        return result;
    }

    private Ressources toRessource(String rawKey) {
        if (rawKey == null) {
            return null;
        }

        String normalized = rawKey.trim().toUpperCase();
        if ("SILK".equals(normalized)) {
            normalized = Ressources.TEXTILE.name();
        }

        try {
            return Ressources.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Integer parseQuantity(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}