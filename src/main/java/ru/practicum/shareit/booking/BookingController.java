package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
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


//    private final BookingService bookingService;
    @PostMapping
    public BookingDto createBooking() {
        return null;
    }

    @PatchMapping("/{bookingId}")  //?approved={approved}
    public BookingDto approvedUser() {
        return null;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById() {
        return null;
    }
    @GetMapping
    public List<ItemInfoDto.BookingDto> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name= "state", defaultValue = "all") String stateParam) {
        try {
            StatusBooking status = StatusBooking.valueOf(stateParam);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("safasf");
        }
        return null;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsAllItems(@RequestParam(name= "state", defaultValue = "all") String stateParam) {
        return null;
    }
}
