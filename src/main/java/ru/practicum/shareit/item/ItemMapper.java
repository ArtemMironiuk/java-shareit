package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
        if (request != null) {
            item.setRequest(request);
        }
        return item;
    }

    public static ItemInfoDto toItemInfoDto(Item item, Booking lastBooking,
                                            Booking nextBooking, List<CommentDto> commentsDto) {
        ItemInfoDto itemInfo = ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentsDto)
                .build();
        if (lastBooking != null) {
            itemInfo.setLastBooking(new ItemInfoDto.BookingDtoOutputItem(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            itemInfo.setNextBooking(new ItemInfoDto.BookingDtoOutputItem(nextBooking.getId(), nextBooking.getBooker().getId()));
        }
        return itemInfo;
    }
}
