package com.diumi.backend.action.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class JsonNodeConverterTest {
    private final JsonNodeConverter converter = new JsonNodeConverter();

    @Test
    void convertsActionDataToJsonTextAndBack() {
        JsonNode actionData = JsonMapper.builder().build()
                .readTree("{\"size\": \"SIZE_LARGE\", \"quantity\": 2, \"items\": [{\"menu\": \"MENU_AMERICANO\"}]}");

        String column = converter.convertToDatabaseColumn(actionData);
        JsonNode restored = converter.convertToEntityAttribute(column);

        assertThat(column).isEqualTo("{\"size\":\"SIZE_LARGE\",\"quantity\":2,\"items\":[{\"menu\":\"MENU_AMERICANO\"}]}");
        assertThat(restored).isEqualTo(actionData);
    }

    @Test
    void keepsEmptyActionDataAsNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
