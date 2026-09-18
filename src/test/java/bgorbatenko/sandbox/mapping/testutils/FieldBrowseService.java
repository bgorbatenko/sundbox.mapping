package bgorbatenko.sandbox.mapping.testutils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Вспомогательный класс для поиска полей в объекте, удовлетворяющих определенному условию.
 *
 * @author Boris Gorbatenko
 */
@Slf4j
public class FieldBrowseService {

    private static final String VTB_PACKAGE_PREFIX = "ru.vtb";

    /**
     * Метод возвращает список полей, которым не присвоены значения. Игнорируются поля статические,
     * финальные и примитивных типов.
     *
     * @param object объект для анализа.
     * @return список незаполненных полей.
     */
    public List<String> getUnassignedFields(Object object) {
        try {
            var fieldsAssignment = getAllFieldsAssignments(object);
            return fieldsAssignment.entrySet().stream()
                    .filter(entry -> !entry.getValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Метод составляет карту заполненности полей объекта значениями. Карта представляет собой
     * объект Map<String, Boolean>. Где ключ - имя поля, значение - признак того, что поле содержит
     * некоторое значение. Поля вложенных объектов анализируются только если данные объекты
     * находятся в пакете ru.vtb.
     * @param object анализируемый объект.
     * @return карта заполненности полей объекта.
     * @throws IllegalAccessException ошибка доступа к полю.
     */
    private Map<String, Boolean> getAllFieldsAssignments(Object object) throws IllegalAccessException {
        var assignments = new HashMap<String, Boolean>();
        var fields = object.getClass().getDeclaredFields();
        for (var field : fields) {
            if (field.getType().isPrimitive()
                    || Modifier.isStatic(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) {
                // поля статические, финальные и примитивного типа не анализируются
                continue;
            }

            if (!field.canAccess(object)) {
                field.setAccessible(true);
            }
            var name = field.getName();
            var value = field.get(object);
            var isAssigned = !Objects.isNull(value);

            if (isAssigned && value.getClass().getPackageName().startsWith(VTB_PACKAGE_PREFIX)) {
                var subAssignments = getAllFieldsAssignments(value);
                subAssignments.forEach((k, v) -> {
                    // полям дочернего объекта добавляется префикс в виде имени поля, содержащего данный объект
                    assignments.put(String.format("%s.%s", name, k), v);
                });
            } else {
                assignments.put(name, isAssigned);
            }
        }
        return assignments;
    }
}
