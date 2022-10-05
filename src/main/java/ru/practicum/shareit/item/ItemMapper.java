package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item, Booking lastBooking, Booking nextBooking) {
        if (lastBooking == null && nextBooking == null) {
            return new ItemInfoDto(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    null);
        }
        return new ItemInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ItemInfoDto.BookingDto(lastBooking.getId(),
//                        lastBooking.getStart(),
//                        lastBooking.getEnd(),
                        lastBooking.getBooker().getId()),
                new ItemInfoDto.BookingDto(nextBooking.getId(),
//                        nextBooking.getStart(),
//                        nextBooking.getEnd(),
                        nextBooking.getBooker().getId())
                );
    }
    public static ItemCommentDto toItemCommentDto(Item item, List<CommentDto> comments) {
        return new ItemCommentDto(item.getId(), item.getName(), item.getAvailable(), item.getDescription(), comments);
    }
}
