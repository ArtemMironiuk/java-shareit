package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    //ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    //FUTURE
    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    //CURRENT
    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end,
                                                                              LocalDateTime start);

    //PAST
    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    //WAITING and REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, StatusBooking status);

    //owner list bookings ALL
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    //FUTURE OWNER
    List<Booking> findByItemIdAndStartIsAfterOrderByStartDesc(Long itemId, LocalDateTime now);

    //CURRENT OWNER
    List<Booking> findByItemIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long itemId, LocalDateTime end,
                                                                            LocalDateTime start);

    //PAST OWNER
    List<Booking> findByItemIdAndEndIsBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    //WAITING and REJECTED OWNER
    List<Booking> findByItemIdAndStatusOrderByStartDesc(Long itemId, StatusBooking status);
}
