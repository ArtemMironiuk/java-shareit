package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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
    public BookingDto createBooking(Long userId, BookingDtoInput bookingDtoInput) {
        Optional<Item> itemOpt = itemRepository.findById(bookingDtoInput.getItemId());
        if (itemOpt.isEmpty()) {
            throw new ObjectNotFoundException("Item c id = {} не существует", bookingDtoInput.getItemId());
        }
        if (itemOpt.get().getAvailable().equals(false)) {
            throw new ValidationException("Item не доступна для бронирования");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ObjectNotFoundException("User с id = {} не зарегистрирован", userId);
        }
        if (bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart())) {
            throw new ValidationException("Конец бронирования не может быть в прошлом");
        }
        if (bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd())) {
            throw new ValidationException(("Время начала бронирования не может быть после времени конца бронирования"));
        }
        if (bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время начала не может быть в прошлом");
        }
        if (userId.equals(itemOpt.get().getOwner().getId())) {
            throw new ObjectNotFoundException("Владелец вещи не может сам у себя ее забронировать");
        }
        Item item = itemOpt.get();
        User user = userOpt.get();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId());
        for (Booking booking : bookings) {
            if (bookingDtoInput.getStart().isBefore(booking.getEnd())) {
                throw new ValidationException("Вещь не доступна к бронированию в это время");
            }
        }
        Booking booking = BookingMapper.toBooking(bookingDtoInput, user, item, StatusBooking.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking), item);
    }

    @Override
    public BookingDto setApproved(Long userId, Long bookingId, String approved) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Booking booking = bookingOpt.get();
        if (bookingOpt.isEmpty()) {
            throw new ObjectNotFoundException("Booking c id = {} нет в базе", bookingId);
        }
        Optional<Item> itemOpt = itemRepository.findById(booking.getItem().getId());
        Item item = itemOpt.get();
        if (!userId.equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException("У Item другой владелец");
        }
        if (approved.equals("true")) {
            if (booking.getStatus().equals(StatusBooking.APPROVED)) {
                throw new ValidationException("Бронирование уже было подтверждено");
            }
            booking.setStatus(StatusBooking.APPROVED);
        }
        if (approved.equals("false")) {
            if (booking.getStatus().equals(StatusBooking.REJECTED)) {
                throw new ValidationException("Бронирование уже было отклонено");
            }
            booking.setStatus(StatusBooking.REJECTED);
        }
        Booking resultBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(resultBooking, item);
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        if (bookingId == null || bookingId == 0) {
            throw new ObjectNotFoundException("Неверно указан id booking");
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new ObjectNotFoundException("Нет бронирования с таким id");
        }
        Optional<Item> item = itemRepository.findById(booking.get().getItem().getId());
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Нет такого пользователя");
        }
        if (!booking.get().getBooker().getId().equals(userId) && !userId.equals(item.get().getOwner().getId())) {
            throw new ObjectNotFoundException("Бронирование под id={} не принадлежит этому пользователю и пользователь" +
                    " не является владельцем", bookingId);
        }
        return BookingMapper.toBookingDto(booking.get(), item.get());
    }

    @Override
    public List<BookingDto> getBookingsOfUser(Long userId, String stateParam) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Нет user c таким id");
        }
        if (stateParam.equals("ALL")) {
            return bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking,
                            itemRepository.findById(booking.getItem().getId()).get()))
                    .collect(toList());
        }
        if (stateParam.equals("FUTURE")) {
            return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking,
                            itemRepository.findById(booking.getItem().getId()).get()))
                    .collect(toList());
        }
        if (stateParam.equals("CURRENT")) {
            return bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking,
                            itemRepository.findById(booking.getItem().getId()).get()))
                    .collect(toList());
        }
        if (stateParam.equals("PAST")) {
            return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking,
                            itemRepository.findById(booking.getItem().getId()).get()))
                    .collect(toList());
        }
        if (stateParam.equals("WAITING")) {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.WAITING)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking,
                            itemRepository.findById(booking.getItem().getId()).get()))
                    .collect(toList());
        }
        if (stateParam.equals("REJECTED")) {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, StatusBooking.REJECTED)
                    .stream()
                    .map(booking -> BookingMapper.toBookingDto(booking,
                            itemRepository.findById(booking.getItem().getId()).get()))
                    .collect(toList());
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingDto> getBookingsAllItems(Long ownerId, String stateParam) {
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Нет owner c id={}", ownerId);
        }
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<BookingDto> resultBooking = new ArrayList<>();
        if (stateParam.equals("ALL")) {
            for (Item item : items) {
                List<BookingDto> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId())
                        .stream()
                        .map(booking -> BookingMapper.toBookingDto(booking, item))
                        .collect(toList());
                resultBooking.addAll(bookings);
            }
            return resultBooking;
        }
        if (stateParam.equals("FUTURE")) {
            for (Item item : items) {
                List<BookingDto> bookings = bookingRepository.findByItemIdAndStartIsAfterOrderByStartDesc(item.getId(),
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toBookingDto(booking, item))
                        .collect(toList());
                resultBooking.addAll(bookings);
            }
            return resultBooking;
        }
        if (stateParam.equals("CURRENT")) {
            for (Item item : items) {
                List<BookingDto> bookings = bookingRepository.findByItemIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(
                                item.getId(), LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toBookingDto(booking, item))
                        .collect(toList());
                resultBooking.addAll(bookings);
            }
            return resultBooking;
        }
        if (stateParam.equals("PAST")) {
            for (Item item : items) {
                List<BookingDto> bookings = bookingRepository.findByItemIdAndEndIsBeforeOrderByStartDesc(item.getId(),
                                LocalDateTime.now())
                        .stream()
                        .map(booking -> BookingMapper.toBookingDto(booking, item))
                        .collect(toList());
                resultBooking.addAll(bookings);
            }
            return resultBooking;
        }
        if (stateParam.equals("WAITING")) {
            for (Item item : items) {
                List<BookingDto> bookings = bookingRepository.findByItemIdAndStatusOrderByStartDesc(item.getId(),
                                StatusBooking.WAITING)
                        .stream()
                        .map(booking -> BookingMapper.toBookingDto(booking, item))
                        .collect(toList());
                resultBooking.addAll(bookings);
            }
            return resultBooking;
        }
        if (stateParam.equals("REJECTED")) {
            for (Item item : items) {
                List<BookingDto> bookings = bookingRepository.findByItemIdAndStatusOrderByStartDesc(item.getId(),
                                StatusBooking.REJECTED)
                        .stream()
                        .map(booking -> BookingMapper.toBookingDto(booking, item))
                        .collect(toList());
                resultBooking.addAll(bookings);
            }
            return resultBooking;
        }
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }
}
