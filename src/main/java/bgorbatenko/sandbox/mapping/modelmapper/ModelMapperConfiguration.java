package bgorbatenko.sandbox.mapping.modelmapper;

import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MatchingStrategy;

/**
 * Создание и настройка экземпляров ModelMapper.
 *
 * @author Boris Gorbatenko
 */
@UtilityClass
public class ModelMapperConfiguration {

    /**
     * Метод создает и конфигурирует зкземпляр ModelMapper.
     *
     * @return сконфигурированный должным образом экземпляр ModelMapper.
     */
    public static ModelMapper getModelMapper(MatchingStrategy matchingStrategy) {
        var modelMapper = new ModelMapper();

        if (matchingStrategy != null) {
            modelMapper.getConfiguration().setMatchingStrategy(matchingStrategy);
        }

        return modelMapper;
    }
}
