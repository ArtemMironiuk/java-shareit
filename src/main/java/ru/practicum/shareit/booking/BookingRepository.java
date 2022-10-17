package ru.practicum.shareit.booking;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    //ALL
    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    //FUTURE
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    //CURRENT
    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId, LocalDateTime end,
                                                              LocalDateTime start, Sort sort);

    //PAST
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    //WAITING and REJECTED
    List<Booking> findByBookerIdAndStatus(Long bookerId, StatusBooking status, Sort sort);

    //owner list bookings ALL
    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findByItem_Owner_Id(Long ownerId, Pageable pageable);

    //FUTURE OWNER
    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    //CURRENT OWNER
    List<Booking> findByItem_Owner_IdAndEndIsAfterAndStartIsBefore(Long ownerId, LocalDateTime end,
                                                                   LocalDateTime start, Sort sort);

    //PAST OWNER
    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    //WAITING and REJECTED OWNER
    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, StatusBooking status, Sort sort);
}
