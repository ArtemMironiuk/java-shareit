package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutputItem;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking, Item item) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus(),
                item.getName()

        );
    }

    public static Booking toBooking(BookingDtoInput bookingDtoInput, User user, Item item, StatusBooking status) {
        return Booking.builder()
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .item(item)
                .booker(user)
                .status(status)
                .build();
    }

    public static BookingDtoOutputItem toBookingDtoOutputItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDtoOutputItem(booking.getId(), booking.getBooker().getId());
    }
}
