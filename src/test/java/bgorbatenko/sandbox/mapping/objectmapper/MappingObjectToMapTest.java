package bgorbatenko.sandbox.mapping.objectmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static bgorbatenko.sandbox.mapping.testutils.SandboxAssertions.assertCollectionsHaveSameContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Примеры маппинга JSON -> Map, Object <-> Map
 * <p>Рекомендовано к ознакомлению:</br>
 * <a href="https://www.baeldung.com/jackson-object-mapper-tutorial">Intro to the Jackson ObjectMapper</a></br>
 * <a href="https://www.baeldung.com/java-cast-map-pojo">Casting Maps to Complex Objects</a>
 * </p>
 *
 * @author Boris Gorbatenko
 */
public class MappingObjectToMapTest {

    private static final String SIMPLE_JSON = "{\"param1\": \"value1\", \"param2\": 2}";
    private static final String HIERARCHICAL_JSON = "{\"param1\": \"value1\", " +
            "\"param2\": {\"param2_1\": \"value2_1\", \"param2_2\": \"value2_2\"}}";
    private static final String ARRAY_CONTAINED_JSON = "{\"param1\": \"value1\", " +
            "\"param2\": [\"value2_1\", \"value2_2\"]}";

    private static final Map<String, Object> TEST_MAP =
            Map.of("param1", "value1",
                    "param2", Map.of(
                            "param1", "value2_1",
                            "param2", "value2_2"),
                    "extraParam", 10);


    @Data
    @Accessors(chain = true)
    private static class MappingTestSubClass {
        private String param1;
        private String param2;
    }
    @Data
    @Accessors(chain = true)
    private static class MappingTestRootClass {
        private String param1;
        private MappingTestSubClass param2;
    }

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @SneakyThrows
    @Test
    void mappingSimpleJsonToMapTest() {
        // Преобразование простого JSON в Map

        // when
        var map = objectMapper.readValue(SIMPLE_JSON, new TypeReference<Map<String, Object>>() {
        });

        // then
        assertNotNull(map);
        assertEquals("value1", map.get("param1"));
        assertEquals(2, map.get("param2"));
    }

    @SneakyThrows
    @Test
    void mappingHierarchicalJsonToMapTest() {
        // Преобразование JSON c иерархической структурой в Map

        // when
        var map = objectMapper.readValue(HIERARCHICAL_JSON, new TypeReference<Map<String, Object>>() {
        });

        // then
        assertNotNull(map);
        assertEquals("value1", map.get("param1"));
        var subMap = map.get("param2");
        assertInstanceOf(Map.class, subMap);
        assertEquals("value2_1", ((Map<?, ?>) subMap).get("param2_1"));
        assertEquals("value2_2", ((Map<?, ?>) subMap).get("param2_2"));
    }

    @SneakyThrows
    @Test
    void mappingArrayContainedJsonToMapTest() {
        // Преобразование JSON, содержащего массив в Map

        // when
        var map = objectMapper.readValue(ARRAY_CONTAINED_JSON, new TypeReference<Map<String, Object>>() {
        });

        // then
        assertNotNull(map);
        assertEquals("value1", map.get("param1"));
        var subList = map.get("param2");
        assertInstanceOf(List.class, subList);
        assertCollectionsHaveSameContent(List.of("value2_1", "value2_2"), (List<String>) subList);
    }

    @SneakyThrows
    @Test
    void mappingMapToObjectTest() {
        // Преобразование Map в Object

        // given
        // Предписание не преобразовывать поля из map, для которых нет соответствия в целевом объекте
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var expectedResult = new MappingTestRootClass()
                .setParam1("value1")
                .setParam2(new MappingTestSubClass()
                        .setParam1("value2_1")
                        .setParam2("value2_2"));

        // when
        var actualResult = objectMapper.convertValue(TEST_MAP, MappingTestRootClass.class);

        // then
        assertEquals(expectedResult, actualResult);
    }

    @SneakyThrows
    @Test
    void mappingObjectToMapTest() {
        // Преобразование Object в Map

        // given
        var testObject = new MappingTestRootClass()
                .setParam1("value1")
                .setParam2(new MappingTestSubClass()
                        .setParam1("value2_1")
                        .setParam2("value2_2"));
        var expectedMap = Map.of("param1", "value1",
                "param2", Map.of("param1", "value2_1", "param2", "value2_2"));

        // when
        var actualMap = objectMapper.convertValue(testObject, new TypeReference<Map<String, Object>>() {
        });

        // then
        assertEquals(expectedMap, actualMap);
    }


    @SneakyThrows
    @Test
    void safeMapTransferViaJsonTest() {
        // Демонстрирует сохранность структуры Map после преобразования в Json и обратно
        // Однако, следует учесть, что информация о типах при сериализации в JSON будет потеряна.
        // Например, long будет сериализован в виде обычного числа, а десериализован будет уже как Integer.

        // when
        var json = objectMapper.writeValueAsString(TEST_MAP);
        var transferredMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});

        // then
        assertEquals(TEST_MAP, transferredMap);
    }

}
