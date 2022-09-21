package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private Long id = 1L;
    private final Map<Long, Item> itemsMap = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(id);
        itemsMap.put(item.getId(), item);
        id++;
        return item;
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemsMap.get(itemId);
    }

    @Override
    public Item updateItem(Item item) {
        itemsMap.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findItems() {
        return new ArrayList<>(itemsMap.values());
    }
}
