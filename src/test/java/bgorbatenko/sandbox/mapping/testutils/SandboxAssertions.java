package bgorbatenko.sandbox.mapping.testutils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Класс содержит кастомные проверки (assertions), специфичные для тестов данного приложения.
 *
 * @author Boris Gorbatenko
 */
public final class SandboxAssertions {

    /**
     * Проверка того, что все поля объекта имеют значение.
     *
     * @param object анализируемый объект;
     */
    public static void assertAllFieldsAssigned(Object object) {
        var unassignedFields = new FieldBrowseService().getUnassignedFields(object);
        assertEquals(0, unassignedFields.size(),
                () -> String.format("Unassigned fields: %s", String.join(", ", unassignedFields)));
    }

    /**
     * Метод эквивалентности содержимого двух коллекций без учета порядка элементов в этих коллекциях.
     *
     * @param collectionA первая сравниваемая коллекция
     * @param collectionB вторая сравниваемая коллекция
     * @param message сообщение, выводимое при нарушении условия эквивалентности содержимого.
     * @param <T>         тип содержимого коллекций.
     */
    public static <T> void assertCollectionsHaveSameContent(Collection<T> collectionA, Collection<T> collectionB,
                                                            String message) {
        assertTrue(collectionA != null
                && collectionB != null
                && collectionA.size() == collectionB.size()
                && collectionA.containsAll(collectionB)
                && collectionB.containsAll(collectionA), message);
    }

    /**
     * Метод эквивалентности содержимого двух коллекций без учета порядка элементов в этих коллекциях.
     *
     * @param collectionA первая сравниваемая коллекция
     * @param collectionB вторая сравниваемая коллекция
     * @param <T>         тип содержимого коллекций.
     */
    public static <T> void assertCollectionsHaveSameContent(Collection<T> collectionA, Collection<T> collectionB) {
        assertCollectionsHaveSameContent(collectionA, collectionB, null);
    }

    /**
     * Проверка эквивалентности содержимого двух списков без учета порядка следования их элементов.
     *
     * @param expected список с ожидаемым содержимым.
     * @param actual   список с проверяемым содержимым
     * @param <T>      тип элементов списков.
     */
    public static <T> void assertListsContentEquals(List<T> expected, List<T> actual) {
        assertCollectionsHaveSameContent(expected, actual);
    }
}
