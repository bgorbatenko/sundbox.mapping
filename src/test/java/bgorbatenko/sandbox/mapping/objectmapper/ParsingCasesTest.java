package bgorbatenko.sandbox.mapping.objectmapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Иллюстрирует различные кейсы, возникающие при парсинге
 *
 * @author Boris Gorbatenko
 */
public class ParsingCasesTest {

    private static final String SIMPLE_JSON_OBJECT = "{\"attribute\": \"attribute_value\"}";
    private static final String TEST_JSON = "{\"param\": " + SIMPLE_JSON_OBJECT + ", \"salt\": \"salty\"}";

    /**
     * Класс выполняет десериализацию значения JSON поля в строку.
     * <p>Пример простой реализации</p>
     */
    private static class AsStringSimpleDeserializer extends JsonDeserializer<String> {

        /**
         * Метод преобразует содержимое поля JSON объекта в строку.
         * <p>Десериализованное значение может не точно представлять исходную строку. При подготовке
         * к десериализации из исходного JSON удаляются не значащие символы (пробелы, переводы строк и т.д.).
         * Кроме этого может поменяться порядок полей и значений (если поле содержит объект или массив).
         * При этом сама структура остается без изменений.</p>
         *
         * @param jsonParser             парсер, использующийся для десериализации содержимого JSON.
         * @param deserializationContext контекст, который может быть использован для получения
         *                               дополнительной информации о текущей операции десериализации.
         * @return значение, десериализованное в строку.
         * @throws IOException ошибка чтения дерева парсинга.
         */
        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {

            // Выделение содержимого поля
            return jsonParser.readValueAsTree().toString();
        }
    }

//    private static class AsStringSafeDeserializer extends JsonDeserializer<String> {
//        @Override
//        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
//                throws IOException {
//            return jsonParser.readValueAs(String.class);
//        }
//    }

    @Data
    @Accessors(chain = true)
    private static class Param {
        private String param;
    }

    @Data
    @Accessors(chain = true)
    private static class StringContainer {
        @JsonDeserialize(using = AsStringSimpleDeserializer.class)
        @JsonProperty("param")
        private String stringParamValue;
    }

    @Data
    @Accessors(chain = true)
    private static class ParentObject {
        @JsonProperty("param")
        private Param objectParamRepresentation;
        @JsonProperty("param")
        private Map<String, Object> mapParamRepresentation;
    }

//    @Data
//    @Accessors(chain = true)
//    private static class StringSafeContainer {
//        @JsonDeserialize(using = AsStringSafeDeserializer.class)
//        @JsonProperty("param")
//        private String safeStringParamValue;
//    }

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void objectFieldToStringMappingTest() {
        // Демонстрирует парсинг объектного свойства в строку.

        // given
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var expectedResult = dry(SIMPLE_JSON_OBJECT);

        // when
        var result = objectMapper.readValue(TEST_JSON, StringContainer.class);

        // then
        assertNotNull(result);
        assertEquals(expectedResult, result.getStringParamValue());
    }

//    @SneakyThrows
//    @Test
//    void objectFieldToStringSafeMappingTest() {
//        // Демонстрирует парсинг объектного свойства в строку, с сохранением исходной структуры строки.
//
//        // given
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//        // when
//        var result = objectMapper.readValue(TEST_JSON, StringSafeContainer.class);
//
//        // then
//        assertNotNull(result);
//        assertEquals(SIMPLE_JSON_OBJECT, result.getSafeStringParamValue());
//    }

    @SneakyThrows
    @Test
    void mappingInTwoFieldsTest() {
        // given
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // when
        var result = objectMapper.readValue(TEST_JSON, ParentObject.class);

        // then
        assertNotNull(result);

    }

    private String dry(String s) {
        if (s != null && !s.isEmpty()) {
            return s.replaceAll("\s*", "");
        } else {
            return s;
        }
    }
}
