package bgorbatenko.sandbox.mapping.modelmapper;

import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Примеры простого использования ModelMapper.
 *
 * @author Boris Gorbatenko
 */
public class SimpleMappingTests {

    private static final Boolean TEST_BOOLEAN_VALUE = Boolean.TRUE;
    private static final Integer TEST_INTEGER_VALUE = 100;
    private static final String TEST_STRING_VALUE = "test string";

    @Data
    @Accessors(chain = true)
    private static class FlatSource {
        private Boolean booleanValue;
        private Integer integerValue;
        private String stringValue;
    }

    @Data
    @Accessors(chain = true)
    private static class FlatTarget {
        private boolean booleanValue;
        private int integerValue;
        private String stringValue;
    }

    @Data
    @Accessors(chain = true)
    private static class PartialCompatibleSource {
        private String compatibleParam;
        private Double incompatibleParam;
    }

    @Data
    @Accessors(chain = true)
    private static class PartialCompatibleTarget {
        private String compatibleParam;
        private boolean incompatibleParam;
    }

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = ModelMapperConfiguration.getModelMapper(null);
    }

    @Test
    void simpleMappingFlatStructuresTest() {
        // Маппер автоматически присвоит значения одноименным полям

        // given
        var source = getTestFlatSource();
        var expectedResult = getTestFlatTarget();

        // when
        var actualResult = modelMapper.map(source, FlatTarget.class);

        // then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void incompatibleTypesMappingTest() {
        // Маппер генерирует исключение при попытке маппинга одноименных, но не совместимых полей.

        // given
        var source = getTestPartialCompatibleSource();

        // then
        assertThrows(MappingException.class, () -> modelMapper.map(source, PartialCompatibleTarget.class));
    }

    @Test
    void skipIncompatibleTypesMappingTest() {
        // Имеется возможность указать мапперу какие поля следует исключить из преобразования.
        // Для этого необходимо добавить правило для преобразований подобных классов.

        // Дополнительное конфигурирование маппера, в котором указывается пропускаемый сеттер для
        // результирующего класса
        modelMapper.typeMap(PartialCompatibleSource.class, PartialCompatibleTarget.class)
                .addMappings(mapper -> mapper.skip(PartialCompatibleTarget::setIncompatibleParam));

        // given
        var source = getTestPartialCompatibleSource();
        var expectedResult = getTestPartialCompatibleTarget();

        // when
        var actualResult = modelMapper.map(source, PartialCompatibleTarget.class);

        // then
        assertEquals(expectedResult, actualResult);
    }

    private FlatSource getTestFlatSource() {
        return new FlatSource()
                .setBooleanValue(TEST_BOOLEAN_VALUE)
                .setIntegerValue(TEST_INTEGER_VALUE)
                .setStringValue(TEST_STRING_VALUE);
    }

    private FlatTarget getTestFlatTarget() {
        return new FlatTarget()
                .setBooleanValue(TEST_BOOLEAN_VALUE)
                .setIntegerValue(TEST_INTEGER_VALUE)
                .setStringValue(TEST_STRING_VALUE);
    }

    private PartialCompatibleSource getTestPartialCompatibleSource() {
        return new PartialCompatibleSource()
                .setCompatibleParam(TEST_STRING_VALUE)
                .setIncompatibleParam(1.0);
    }

    private PartialCompatibleTarget getTestPartialCompatibleTarget() {
        return new PartialCompatibleTarget()
                .setCompatibleParam(TEST_STRING_VALUE);
    }
}