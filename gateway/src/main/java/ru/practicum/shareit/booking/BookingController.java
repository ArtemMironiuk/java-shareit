package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.handler.ValidationException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody BookItemRequestDto bookingDtoInput) {
        log.info("Получен запрос к эндпоинту POST, /bookings");
        return bookingClient.createBooking(userId, bookingDtoInput);
    }

    @PatchMapping("/{bookingId}")  //?approved={approved}
    public ResponseEntity<Object> setApproved(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable @Positive Long bookingId,
                                              @RequestParam(name = "approved") String approved) {
        log.info("Получен запрос к эндпоинту PATCH, /bookings/{bookingId}?approved={}", approved);
        try {
            Boolean approvedNew = null;
            if (approved.equals("true")) {
                approvedNew = Boolean.TRUE;
            } else if (approved.equals("false")) {
                approvedNew = Boolean.FALSE;
            }
            return bookingClient.setApproved(userId, bookingId, approvedNew);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: APPROVED");
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Получен запрос к эндпоинту GET, /bookings/{bookingId}");
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /bookings?state={}", stateParam);
        try {
            BookingState state = BookingState.valueOf(stateParam);
            return bookingClient.getBookingsOfUser(userId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /bookings/owner?state={}", stateParam);
        try {
            BookingState state = BookingState.valueOf(stateParam);
            return bookingClient.getBookingsAllItems(ownerId, state, from, size);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
