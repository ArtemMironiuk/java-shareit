package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingDtoOutputItem;
import ru.practicum.shareit.user.User;

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
        ItemInfoDto itemInfo = ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentsDto)
                .build();
        if (lastBooking != null){
            itemInfo.setLastBooking(lastBooking);
        }
        if (nextBooking != null){
            itemInfo.setNextBooking(nextBooking);
        }
        return itemInfo;

//        if (lastBooking == null && nextBooking == null) {
//            return ItemInfoDto.builder()
//                    .id(item.getId())
//                    .name(item.getName())
//                    .description(item.getDescription())
//                    .available(item.getAvailable())
//                    .comments(commentsDto)
//                    .build();
//        }
//        if (lastBooking == null && nextBooking != null) {
//            return ItemInfoDto.builder()
//                    .id(item.getId())
//                    .name(item.getName())
//                    .description(item.getDescription())
//                    .available(item.getAvailable())
//                    .nextBooking(new BookingDtoOutputItem(nextBooking.getId(), nextBooking.getBookerId()))
//                    .comments(commentsDto)
//                    .build();
//        }
//        if (lastBooking != null && nextBooking == null) {
//            return ItemInfoDto.builder()
//                    .id(item.getId())
//                    .name(item.getName())
//                    .description(item.getDescription())
//                    .available(item.getAvailable())
//                    .lastBooking(new BookingDtoOutputItem(lastBooking.getId(), lastBooking.getBookerId()))
//                    .comments(commentsDto)
//                    .build();
//        }
//        return new ItemInfoDto(item.getId(),
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                new BookingDtoOutputItem(lastBooking.getId(),
//                        lastBooking.getBookerId()),
//                new BookingDtoOutputItem(nextBooking.getId(),
//                        nextBooking.getBookerId()),
//                commentsDto
//        );
    }
}
