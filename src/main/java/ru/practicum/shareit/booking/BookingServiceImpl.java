package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Item c id = {} не существует", bookingDto.getItemId());
        }
        if (item.get().getAvailable().equals(false)) {
            throw new ValidationException("Item не доступна для бронирования");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("User с id = {} не зарегистрирован", userId);
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Конец бронирования не может быть в прошлом");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException(("Время начала бронирования не может быть после времени конца бронирования"));
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала не может быть в прошлом");
        }
        Booking booking = BookingMapper.toBooking(bookingDto,userId,StatusBooking.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking), item.get());
    }

    @Override
    public BookingDto setApproved(Long userId, Long bookingId, String approved) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Booking booking = bookingOpt.get();
        if (bookingOpt.isEmpty()) {
            throw new ObjectNotFoundException("Booking c id = {} нет в базе", bookingId);
        }
        Optional<Item> item = itemRepository.findById(bookingOpt.get().getItemId());
        if (!userId.equals(item.get().getOwner().getId())) {
            throw new ValidationException("У Item другой владелец");
        }
        if (approved.equals("true")) {
            booking.setStatus(StatusBooking.APPROVED);
        }
        if (approved.equals("false")) {
            booking.setStatus(StatusBooking.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking), item.get());
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        if (bookingId == null || bookingId == 0) {
            throw new ValidationException("Неверно указан id booking");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        Item item = itemRepository.findById(booking.getItemId()).get();
        return BookingMapper.toBookingDto(booking, item);
    }

    @Override
    public List<BookingDto> getBookingsOfUser(Long userId, String stateParam) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Нет user c id={}", userId);
        }
        List<BookingDto> resultBookings = new ArrayList<>();
        if (stateParam.equals("ALL")) {
            List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            for (Booking booking : bookings) {
                Item item = itemRepository.findById(booking.getItemId()).get();
                resultBookings.add(BookingMapper.toBookingDto(booking, item));
            }
            return resultBookings;
        }
        if (stateParam.equals("FUTURE")) {
            List<Booking> bookings = bookingRepository.findByBookerIdAndStartIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            for (Booking booking : bookings) {
                Item item = itemRepository.findById(booking.getItemId()).get();
                resultBookings.add(BookingMapper.toBookingDto(booking, item));
            }
            return resultBookings;
        }
        if (stateParam.equals("CURRENT")) {
            List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            for (Booking booking : bookings) {
                Item item = itemRepository.findById(booking.getItemId()).get();
                resultBookings.add(BookingMapper.toBookingDto(booking, item));
            }
            return resultBookings;
        }
        if (stateParam.equals("PAST")) {
            List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            for (Booking booking : bookings) {
                Item item = itemRepository.findById(booking.getItemId()).get();
                resultBookings.add(BookingMapper.toBookingDto(booking, item));
            }
            return resultBookings;
        }
        if (stateParam.equals("WAITING") || stateParam.equals("REJECTED")) {
            List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, stateParam);
            for (Booking booking : bookings) {
                Item item = itemRepository.findById(booking.getItemId()).get();
                resultBookings.add(BookingMapper.toBookingDto(booking, item));
            }
            return resultBookings;
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingDto> getBookingsAllItems(Long ownerId, String stateParam) {
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Нет owner c id={}", ownerId);
        }
        return null;
    }
}
