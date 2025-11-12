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

    @BeforeEach
    public void stepUp(){
        user = new User(1L);
    }

    /**
     * Тестирование команды /test.
     * Должно принимать верные ответы.
     * <p>Сложность тестирования этого метода в том,
     * что код обязан гарантировать такую последовательность задаваемых примеров</p>
     */
    @Test
    void testCommandShouldAcceptCorrectAnswersTest() {
        botLogic.processCommand(user, "/test");
        botLogic.processCommand(user, "100");
        Assertions.assertEquals("Правильный ответ!", fakeBot.getMessage(1));
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
     * Проверка на то, что состояние пользователя меняется после введенной команды {@code /test}
     * И в конце работы команды его состояние возвращается на изначальное
     */
    @Test
    void shouldChangeUserStateAfterTestCommand(){
        botLogic.processCommand(user, "/test");

        Assertions.assertEquals(State.TEST ,user.getState());

        botLogic.processCommand(user, "1");
        Assertions.assertEquals("Вы ошиблись, верный ответ: 100", fakeBot.getMessage(1));
        botLogic.processCommand(user, "1");

        Assertions.assertEquals("Вы ошиблись, верный ответ: 6", fakeBot.getMessage(3));
        Assertions.assertEquals(State.INIT ,user.getState());
    }

    /**
     * Проверяется, что сообщение действительно отправляется ТОЛЬКО после заданного количества секунд.
     */
    @Test
    void shouldSendNotification(){
        botLogic.processCommand(user, "/notify");
        botLogic.processCommand(user, "Какой-то смешной текст");
        botLogic.processCommand(user, "1");

        Assertions.assertFalse(fakeBot.isContains("Сработало напоминание: 'Какой-то смешной текст'"));
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(fakeBot.getMessage(3) ,"Сработало напоминание: 'Какой-то смешной текст'");
    }

    /**
     * Проверка на то, что состояние пользователя меняется после введенной команды {@code /notify}
     * И в конце работы команды его состояние возвращается на изначальное
     */
    @Test
    void shouldChangeUserStateAfterCommandNotify(){
        botLogic.processCommand(user, "/notify");

        Assertions.assertEquals(State.SET_NOTIFY_TEXT ,user.getState());

        botLogic.processCommand(user, "Какой-то смешной текст");

        Assertions.assertEquals(State.SET_NOTIFY_DELAY ,user.getState());

        botLogic.processCommand(user, "1");

        Assertions.assertEquals(State.INIT ,user.getState());
    }

    /**
     * Проверка на то, что сообщение не будет поставлено на отправку, если задать негативное количество секунд
     */
    @Test
    void shouldNotSendNotificationWithNegativeDelay(){
        botLogic.processCommand(user, "/notify");
        botLogic.processCommand(user, "Какой-то смешной текст");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> botLogic.processCommand(user, "-1"));
    }
    /**
     * Проверка на то, что сообщение не будет поставлено на отправку, если задать текст вместо числа в виде задержки
     */
    @Test
    void shouldNotSendNotificationWithTextDelay(){
        botLogic.processCommand(user, "/notify");
        botLogic.processCommand(user, "Какой-то смешной текст");
        botLogic.processCommand(user, "текст");

        Assertions.assertEquals("Пожалуйста, введите целое число", fakeBot.getMessage(2));
    }

    /**
     * Проверка на то, что не будут даны вопросы, если пользователь до этого не ошибался.
     * <p>Так же происходит проверка на то, что состояние пользователя в этом случае не изменится</p>
     */
    @Test
    public void shouldNotSendNewQuestionsWithoutWhrongAnswersFromUser() {
        botLogic.processCommand(user, "/repeat");

        Assertions.assertEquals(State.INIT ,user.getState());
        Assertions.assertEquals("Нет вопросов для повторения", fakeBot.getMessage(0));
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
}