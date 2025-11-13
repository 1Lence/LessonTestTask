package example.bot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Проверка работы логики бота
 */
class BotLogicTest {
    private User user;
    private FakeBot fakeBot = new FakeBot();
    private BotLogic botLogic = new BotLogic(fakeBot);

    /**
     * Подготовка данных для тестирования
     */
    @BeforeEach
    public void stepUp() {
        user = new User(1L);
    }

    /**
     * Тестирование команды /test.
     * Должно принимать верные ответы.
     */
    @Test
    void testCommandShouldAcceptCorrectAnswersTest() {
        botLogic.processCommand(user, "/test");

        Assertions.assertEquals("Вычислите степень: 10^2", fakeBot.getMessage(0));
        botLogic.processCommand(user, "100");

        Assertions.assertEquals("Правильный ответ!", fakeBot.getMessage(1));
        Assertions.assertEquals("Сколько будет 2 + 2 * 2", fakeBot.getMessage(2));

        botLogic.processCommand(user, "6");

        Assertions.assertEquals("Правильный ответ!", fakeBot.getMessage(3));
    }

    /**
     * Тестирование команды /test.
     * Должно не принимать неверные ответы.
     */
    @Test
    void testCommandShouldNotAcceptIncorrectAnswersTest() {
        botLogic.processCommand(user, "/test");
        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Вы ошиблись, верный ответ: 100", fakeBot.getMessage(1));
        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Вы ошиблись, верный ответ: 6", fakeBot.getMessage(3));
    }

    /**
     * Проверяется, что сообщение действительно отправляется ТОЛЬКО после заданного количества секунд.
     */
    @Test
    void shouldSendNotification() throws InterruptedException {
        botLogic.processCommand(user, "/notify");
        Assertions.assertEquals(fakeBot.getMessage(0), "Введите текст напоминания");

        botLogic.processCommand(user, "Какой-то смешной текст");
        Assertions.assertEquals(fakeBot.getMessage(1), "Через сколько секунд напомнить?");

        botLogic.processCommand(user, "1");
        Assertions.assertEquals(
                "Напоминание установлено",
                fakeBot.getMessage(2)
        );

        Thread.sleep(1015);

        Assertions.assertEquals(fakeBot.getMessage(3), "Сработало напоминание: 'Какой-то смешной текст'");
    }

    /**
     * Проверка на то, что сообщение не будет поставлено на отправку, если задать негативное количество секунд
     */
    @Test
    void shouldNotSendNotificationWithNegativeDelay() {
        botLogic.processCommand(user, "/notify");
        botLogic.processCommand(user, "Какой-то смешной текст");

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> botLogic.processCommand(user, "-1"));

        Assertions.assertEquals("Negative delay.", exception.getMessage());
    }

    /**
     * Проверка на то, что сообщение не будет поставлено на отправку, если задать текст вместо числа в виде задержки
     */
    @Test
    void shouldNotSendNotificationWithTextDelay() {
        botLogic.processCommand(user, "/notify");
        botLogic.processCommand(user, "Какой-то смешной текст");
        botLogic.processCommand(user, "текст");

        Assertions.assertEquals("Пожалуйста, введите целое число", fakeBot.getMessage(2));
    }

    /**
     * Проверка на то, что после верных ответов вопросы будут удаляться
     */
    @Test
    public void shouldDeleteWrondAnswersAfterCorrectAnswers() {
        botLogic.processCommand(user, "/test");
        botLogic.processCommand(user, "1");

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Вычислите степень: 10^2", fakeBot.getMessage(3));

        botLogic.processCommand(user, "100");
        Assertions.assertEquals("Правильный ответ!", fakeBot.getMessage(4));

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Нет вопросов для повторения", fakeBot.getMessage(6));
    }

    /**
     * Проверка на то, что после верного ответа удалится <b>только</b> тот вопрос,
     * на который смог ответить пользователь
     */
    @Test
    public void shouldDeleteWrondAnswersOnlyAfterCorrectAnswers() {
        botLogic.processCommand(user, "/test");
        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Сколько будет 2 + 2 * 2", fakeBot.getMessage(2));
        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Вы ошиблись, верный ответ: 6", fakeBot.getMessage(3));

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Вычислите степень: 10^2", fakeBot.getMessage(5));

        botLogic.processCommand(user, "100");
        Assertions.assertEquals("Правильный ответ!", fakeBot.getMessage(6));
        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Вы ошиблись, верный ответ: 6", fakeBot.getMessage(8));

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Сколько будет 2 + 2 * 2", fakeBot.getMessage(10));
    }

    /**
     * Проверка на то, что после неверных ответов вопросы не будут удаляться
     */
    @Test
    public void shouldNotDeleteWrondAnswersAfterIncorrectAnswers() {
        botLogic.processCommand(user, "/test");
        botLogic.processCommand(user, "1");

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Вычислите степень: 10^2", fakeBot.getMessage(3));
        botLogic.processCommand(user, "1");

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Вычислите степень: 10^2", fakeBot.getMessage(6));
    }

    /**
     * Проверка на добавление только тех вопросов, на которые дан неверный ответ
     */
    @Test
    public void shouldAddQuestionssOnlyAfterIncorrectAnswers() {
        botLogic.processCommand(user, "/test");
        Assertions.assertEquals("Вычислите степень: 10^2", fakeBot.getMessage(0));
        botLogic.processCommand(user, "100");
        Assertions.assertEquals("Сколько будет 2 + 2 * 2", fakeBot.getMessage(2));
        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Вы ошиблись, верный ответ: 6", fakeBot.getMessage(3));

        botLogic.processCommand(user, "/repeat");
        Assertions.assertEquals("Сколько будет 2 + 2 * 2", fakeBot.getMessage(5));
    }
}