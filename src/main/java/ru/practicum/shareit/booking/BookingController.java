package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {


    private final BookingService bookingService;
    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос к эндпоинту POST, /bookings");
        return bookingService. createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")  //?approved={approved}
    public BookingDto setApproved(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam String approved) {
        log.info("Получен запрос к эндпоинту PATCH, /bookings/{bookingId}?approved={}",approved);
        return bookingService.setApproved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId) {
        log.info("Получен запрос к эндпоинту GET, /bookings/{bookingId}");
        return bookingService.findBookingById(userId, bookingId);
    }
    @GetMapping
    public List<BookingDto> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name= "state", defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос к эндпоинту GET, /bookings?state={}", stateParam);
        try {
            StatusBooking status = StatusBooking.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("safasf");
        }

        return bookingService.getBookingsOfUser(userId, stateParam);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(name= "state", defaultValue = "ALL") String stateParam) {
        return bookingService.getBookingsAllItems(ownerId, stateParam);
    }
}
