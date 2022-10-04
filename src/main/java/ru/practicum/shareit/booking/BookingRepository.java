package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //ALL
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);
    //FUTURE
    List<Booking> findByBookerIdAndStartIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime start);
    //CURRENT
    List<Booking> findByBookerIdAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime end);
    //PAST
    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);
    //WAITING and REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, String status);

}
