package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDtoInput bookingDtoInput);

    BookingDto setApproved(Long userId, Long bookingId, String approved);

    BookingDto findBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsOfUser(Long userId, StateBooking state, Integer from, Integer size);

    List<BookingDto> getBookingsAllItems(Long ownerId, StateBooking state, Integer from, Integer size);
}
