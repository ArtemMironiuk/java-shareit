package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public ItemDto toItemDto( Item item) {

        return null;
    }

//    public static ItemDto toItemDto(Item item) {
//        return new ItemDto(
//                item.getId(),
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                item.getOwner() != null ? item.getOwner().getId() : null,
//                item.getRequest() != null ? item.getRequest().getId() : null);
//    }
}
