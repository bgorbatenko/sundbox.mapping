package bgorbatenko.sandbox.mapping.modelmapper;

import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Пример использования ModelMapper для агрегирования свойств из разных локаций в одном объектном поле.
 *
 * @author Boris Gorbatenko
 */
public class AggregateMappingTests {

    @Data
    @Accessors(chain = true)
    public static class SourceSubclass {
        private String property;
    }

    @Data
    @Accessors(chain = true)
    public static class Source {
        private int id;
        private SourceSubclass sourceSubclassProperty;
        private String property;
    }

    @Data
    @Accessors(chain = true)
    public static class TargetSubclass {
        private String property1;
        private String property2;
    }

    @Data
    @Accessors(chain = true)
    public static class Target {
        private int id;
        private TargetSubclass targetSubclassProperty;
    }

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = ModelMapperConfiguration.getModelMapper(null);
    }

    @Test
    void aggregateMappingTest() {

        // configuration
        Converter<SourceSubclass, String> sourceSubclassPropertyExtractor =
                c -> Optional.ofNullable(c.getSource()).map(SourceSubclass::getProperty).orElse(null);

        modelMapper.typeMap(Source.class, TargetSubclass.class)
                .addMapping(Source::getProperty, TargetSubclass::setProperty1)
                .addMappings(m -> m.using(sourceSubclassPropertyExtractor)
                        .map(Source::getSourceSubclassProperty, TargetSubclass::setProperty2));

        modelMapper.typeMap(Source.class, Target.class)
                .addMappings(mapper -> mapper.map(x -> x, Target::setTargetSubclassProperty));


        // given
        var id = 10;
        var propertyValue1 = "property_value_1";
        var propertyValue2 = "property_value_2";
        var testSource = new Source()
                .setId(id)
                .setProperty(propertyValue1)
                .setSourceSubclassProperty(new SourceSubclass()
                        .setProperty(propertyValue2));
        var expectedTarget = new Target()
                .setId(id)
                .setTargetSubclassProperty(
                        new TargetSubclass()
                                .setProperty1(propertyValue1)
                                .setProperty2(propertyValue2));

        // when
        var actualTarget = modelMapper.map(testSource, Target.class);

        // then
        assertEquals(expectedTarget, actualTarget);
    }
}
