package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);
    //ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);
    //FUTURE
    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);
    //CURRENT
    List<Booking> findByBookerIdAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime end);
    //PAST
    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);
    //WAITING and REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);
    //owner list bookings ALL
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);
    //FUTURE OWNER
    List<Booking> findByItemIdAndStartIsAfterOrderByStartDesc(Long itemId, LocalDateTime now);
    //CURRENT OWNER
    List<Booking> findByItemIdAndEndIsAfterOrderByStartDesc(Long itemId, LocalDateTime now);
    //PAST OWNER
    List<Booking> findByItemIdAndEndIsBeforeOrderByStartDesc(Long itemId, LocalDateTime now);
    //WAITING and REJECTED OWNER
    List<Booking> findByItemIdAndStatusOrderByStartDesc(Long itemId, StatusBooking status);
}
