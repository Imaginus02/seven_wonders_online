package com.reynaud.wonders.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reynaud.wonders.model.Ressources;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Converter for List of resource cost maps (used for wonder stage costs)
 * Preserves the order of stages in the list (position 0 remains at position 0)
 */
@Converter
public class StageCostsConverter implements AttributeConverter<List<Map<Ressources, Integer>>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Map<Ressources, Integer>> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize stage costs", e);
        }
    }

    @Override
    public List<Map<Ressources, Integer>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Map<String, Integer>> rawList = OBJECT_MAPPER.readValue(dbData, new TypeReference<List<Map<String, Integer>>>() {
            });
            if (rawList == null || rawList.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Convert raw map to EnumMap with Ressources enum keys, preserving order
            List<Map<Ressources, Integer>> result = new ArrayList<>();
            for (Map<String, Integer> rawMap : rawList) {
                Map<Ressources, Integer> enumMap = new EnumMap<>(Ressources.class);
                if (rawMap != null) {
                    for (Map.Entry<String, Integer> entry : rawMap.entrySet()) {
                        try {
                            enumMap.put(Ressources.valueOf(entry.getKey()), entry.getValue());
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid enum values
                        }
                    }
                }
                result.add(enumMap);
            }
            return result;
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}
