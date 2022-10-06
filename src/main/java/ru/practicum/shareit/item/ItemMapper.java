package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoOutputItem;
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

    public static ItemInfoDto toItemInfoDto(Item item, BookingDtoOutputItem lastBooking, BookingDtoOutputItem nextBooking, List<CommentDto> commentsDto) {
        if (lastBooking == null && nextBooking == null) {
            return ItemInfoDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .build();
        }
        return new ItemInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new BookingDtoOutputItem(lastBooking.getId(),
                        lastBooking.getBookerId()),
                new BookingDtoOutputItem(nextBooking.getId(),
                        nextBooking.getBookerId()),
                commentsDto
                );
    }
    public static ItemCommentDto toItemCommentDto(Item item, List<CommentDto> comments) {
        return new ItemCommentDto(item.getId(), item.getName(), item.getAvailable(), item.getDescription(), comments);
    }
}
