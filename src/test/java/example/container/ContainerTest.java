package example.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Проверка работоспособности методов добавления и удаления объектов в классе Container
 */
class ContainerTest {
    private Container container;

    /**
     * Подготовка данных к тестам
     */
    @BeforeEach
    void before(){
        container = new Container();
    }

    /**
     * Проверяется что в список добавляется реально тот объект, который мы передали
     * Дополнительные проверки:
     * <ul>
     *     <li>Метод вернёт правильное значение, если операция прошла успешно;</li>
     *     <li>Работа contains;</li>
     *     <li>Проверка на то, что метод add не дублирует элементы, а реально их добавляет.</li>
     * </ul>
     */
    @Test
    void addAndGetTest() {
        Item firstItem = new Item(1);
        Item secondItem = new Item(2);

        Assertions.assertTrue(container.add(firstItem));
        Assertions.assertTrue(container.contains(firstItem));

        container.add(secondItem);

        Item tempItem = container.get(0);

        Assertions.assertEquals(tempItem.getNum(), firstItem.getNum());
        Assertions.assertNotEquals(tempItem.getNum(), secondItem.getNum());

    }

    /**
     * Проверка метода удаления элемента и его возвращаемого значения.
     * Так же в тесте происходит проверка метода {@code size()}
     * И дополнительная проверка на отрицание для метода {@code contains()}
     */
    @Test
    void removeAndSizeTest() {
        Item firstItem = new Item(1);
        Item secondItem = new Item(2);

        container.add(firstItem);
        container.add(secondItem);

        Assertions.assertEquals(2, container.size());

        Assertions.assertTrue(container.remove(firstItem));
        Assertions.assertEquals(1, container.size());

        Assertions.assertFalse(container.contains(firstItem));
    }
}