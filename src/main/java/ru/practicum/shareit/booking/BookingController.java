package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody BookingDtoInput bookingDtoInput) {
        log.info("Получен запрос к эндпоинту POST, /bookings");
        return bookingService.createBooking(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")  //?approved={approved}
    public BookingDto setApproved(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam String approved) {
        log.info("Получен запрос к эндпоинту PATCH, /bookings/{bookingId}?approved={}", approved);
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
                                              @RequestParam(name = "state", required = false,
                                                      defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос к эндпоинту GET, /bookings?state={}", stateParam);
        try {
            StateBooking state = StateBooking.valueOf(stateParam);
            return bookingService.getBookingsOfUser(userId, state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(name = "state", required = false,
                                                        defaultValue = "ALL") String stateParam) {
        log.info("Получен запрос к эндпоинту GET, /bookings/owner?state={}", stateParam);
        try {
            StateBooking state = StateBooking.valueOf(stateParam);
            return bookingService.getBookingsAllItems(ownerId, state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
