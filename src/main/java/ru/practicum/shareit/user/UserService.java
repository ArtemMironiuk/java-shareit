package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {
    /**
     * Получение списка всез пользователей
     *
     * @return список пользователей в DTO
     */
    List<UserDto> findAll();

    /**
     * Получение существующего пользователя
     *
     * @param userId идентификатор пользователя
     * @return экземпляр пользователя в DTO
     */
    UserDto findUserById(Long userId);

    /**
     * Создание пользователя user
     *
     * @param userDto экземпляр пользователя в DTO
     * @return созданный пользователь в DTO
     */
    UserDto createUser(UserDto userDto);

    /**
     * Обновление существующего пользователя
     *
     * @param userId  идентификатор пользователя
     * @param userDto экземпляр текущего пользователя
     * @return экземпляр обновленного пользователя
     */
    UserDto updateUser(Long userId, UserDto userDto);

    /**
     * Удаление пользователя по id
     *
     * @param userId идентификатор пользователя
     */
    void deleteUser(Long userId);
}
