package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Получение списка вещей, которые добавил определенный пользователь
     * @param userId идентификатор пользователя, которому принадлежат вещи
     * @return список вещей пользователя в DTO
     */
    List<Item> findAllByOwnerIdOrderById(Long userId, Pageable pageable);

    /**
     * Поиск вещей по слову или первым буквам
     * @param text слово или набор букв по которым нужно найти вещь
     * @return список найденных вещей
     */
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text, Pageable pageable);

    List<Item> findByRequest_Id(Long requestId);

}
