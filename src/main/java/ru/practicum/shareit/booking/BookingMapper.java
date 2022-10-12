package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new BookingDto.ItemDto(booking.getItem().getId(),
                        booking.getItem().getName(),
                        booking.getItem().getDescription(),
                        booking.getItem().getAvailable()),
                new BookingDto.UserDto(booking.getBooker().getId(),
                        booking.getBooker().getName(),
                        booking.getBooker().getEmail()),
                booking.getStatus()
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
}
