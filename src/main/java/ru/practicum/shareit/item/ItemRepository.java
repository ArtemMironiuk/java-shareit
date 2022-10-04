package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Получение списка вещей, которые добавил определенный пользователь
     * @param userId идентификатор пользователя, которому принадлежат вещи
     * @return список вещей пользователя в DTO
     */
    List<Item> findAllByOwnerId(Long userId);

    /**
     * Поиск вещей по слову или первым буквам
     * @param text слово или набор букв по которым нужно найти вещь
     * @return список найденных вещей
     */
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

}
