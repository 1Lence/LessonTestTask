package example.note;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Тестирование логики добавления, изменения, удаления, получения списка заметок
 * Получение списка заметок отдельно не проверяется,
 * потому что проверка их работы происходит явно во всех других тестах
 */
class NoteLogicTest {

    private NoteLogic logic = new NoteLogic();

    /**
     * Проверяется работа метода добавления заметки
     */
    @Test
    void addMessageTest() {
        Random rand = new Random();
        int randomInt = rand.nextInt(100);

        logic.handleMessage("/add " + "1 New note" + randomInt);

        Assertions.assertEquals(
                "Your notes: 1 New note" + randomInt,
                logic.handleMessage("/notes ")
        );
    }

    /**
     * Проверяется работа метода изменения заметки
     */
    @Test
    void editMessageTest() {
        Random rand = new Random();
        int randomInt = rand.nextInt(100);

        logic.handleMessage("/add " + "1 New note" + randomInt);
        logic.handleMessage("/edit " + "1 Edited note");

        Assertions.assertEquals(
                "Your notes: 1 Edited note",
                logic.handleMessage("/notes ")
        );
    }

    /**
     * Проверяется работа выполнения метода delete
     *
     * <p>После удаления второй записи должна остаться ТОЛЬКО первая запись</p>
     */
    @Test
    void deleteMessageTest() {
        Random rand = new Random();
        int randomInt = rand.nextInt(100);

        logic.handleMessage("/add 1 New note" + randomInt);
        logic.handleMessage("/add 2 New note");
        logic.handleMessage("/del 2");

        Assertions.assertEquals(
                "Your notes: 1 New note" + randomInt,
                logic.handleMessage("/notes ")
        );
    }
}