package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDtoInput bookingDtoInput);

    BookingDto setApproved(Long userId, Long bookingId, String approved);

    BookingDto findBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsOfUser(Long userId, String stateParam);

    List<BookingDto> getBookingsAllItems(Long ownerId, String stateParam);
}
