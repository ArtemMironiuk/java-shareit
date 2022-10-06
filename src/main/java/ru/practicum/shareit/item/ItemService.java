package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    /**
     * Создание вещи item
     * @param userId идентификатор пользователя который добавляет вещь
     * @param itemDto экземпляр текущей вещи в DTO
     * @return экземпляр созданной вещи в DTO
     */
    ItemDto createItem(Long userId, ItemDto itemDto);

    /**
     * Обновление данных уже существующей вещи
     * @param userId идентификатор пользователя который добавил вещь
     * @param itemDtoUpdate экземпляр текущей вещи в DTO
     * @param itemId идентификатор существующей вещи
     * @return экземпляр обновленной вещи в DTO
     */
    ItemDto updateItem(Long userId, ItemDtoUpdate itemDtoUpdate, Long itemId);

    /**
     * Получение вещи по id
     * @param userId идентификатор пользователя
     * @param itemId идентификатор существующей вещи
     * @return экземпляр вещи в DTO
     */
    ItemInfoDto findItemById(Long userId, Long itemId);

    /**
     * Получение списка вещей, которые добавил определенный пользователь
     * @param userId идентификатор пользователя, которому принадлежат вещи
     * @return список вещей пользователя в DTO
     */
    List<ItemInfoDto> findItemsOfUser(Long userId);

    /**
     * Поиск вещей по слову или первым буквам
     * @param userId идентификатор пользователя
     * @param text слово или набор букв по которым нужно найти вещь
     * @return список найденных вещей
     */
    List<ItemDto> searchItem(Long userId, String text);

    /**
     * Создать комментарий
     * @param userId
     * @param commentDto
     * @param itemId
     * @return
     */
    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId);

    /**
     * Получение вещи со списком комментариев
     */
//    ItemCommentDto findItemByIdWithComments(Long userId, Long itemId);
}
