package example.bot;

import java.util.ArrayList;
import java.util.List;

/**
 * Бот, который будет вместо вывода сообщений в консоль хранить их в списке
 */
public class FakeBot implements Bot{
    private List<String> messages = new ArrayList<>();

    @Override
    public void sendMessage(Long chatId, String message) {
        messages.add(message);
    }

    /**
     * Для получения сообщения по индексу
     * @param index индекс
     * @return сообщение по индексу
     */
    public String getMessage(Integer index) {
        return messages.get(index);
    }

    /**
     * Позволяет проверить содержится ли сообщение в коллекции
     * @param message Сообщение от бота
     * @return {@code true} содержится, {@code false} не содержится
     */
    public boolean isContains(String message) {
        return messages.contains(message);
    }
}
