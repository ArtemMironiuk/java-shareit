package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    /**
     * Получение списка всех пользователей
     * @return списов пользователей
     */
    List<User> findAll();

    /**
     * Получение существующего пользователя по id
     * @param userId идентификатор пользователя
     * @return экземпляр существующего пользователя
     */
    User findUserById(Long userId);

    /**
     * Создание пользователя user
     * @param user экземпляр текущего пользователя
     * @return экземпляр созданного пользователя
     */
    User createUser(User user);

    /**
     * Обновление существующего пользователя
     * @param userId идентификатор пользователя
     * @param user экземпляр текущей вещи
     * @return экземпляр обновленной вещи
     */
    User updateUser(Long userId, User user);

    /**
     * Удаление существующего пользователя
     * @param userId идентификатор пользователя
     */
    void deleteUser(Long userId);
}
