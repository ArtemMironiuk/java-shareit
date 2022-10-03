package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
//    /**
//     * Создание вещи item
//     * @param item экземпляр текущей вещи
//     * @return экземпляр созданной вещи
//     */
//    Item createItem(Item item);
//
//    /**
//     * Получение вещи item по id
//     * @param itemId идентификатор вещи
//     * @return экземпляр сущности Item
//     */
//    Item findItemById(Long itemId);
//
//    /**
//     * Обновление данных уже существующей вещи
//     * @param item экземпляр текущей вещи
//     * @return экземпляр обновленной вещи
//     */
//    Item updateItem(Item item);
//
//    /**
//     * Получение списка вещей
//     * @return список всех вещей
//     */
//    List<Item> findItems();
}
