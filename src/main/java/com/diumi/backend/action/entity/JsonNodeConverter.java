package com.diumi.backend.action.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * action_data(JSON) 컬럼을 Jackson 3 JsonNode로 바꿔 주는 변환기.
 * Hibernate 기본 JSON 변환은 Jackson 2를 써서 Jackson 3 JsonNode를 읽지 못하므로 직접 문자열로 변환한다.
 */
@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {
    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        return attribute == null || attribute.isNull() ? null : MAPPER.writeValueAsString(attribute);
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : MAPPER.readTree(dbData);
    }
}
