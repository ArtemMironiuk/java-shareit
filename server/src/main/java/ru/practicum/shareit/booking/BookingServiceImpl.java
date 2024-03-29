package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected BookingRepository bookingRepository;

    @Transactional
    @Override
    public BookingDto createBooking(Long userId, BookingDtoInput bookingDtoInput) {
        Optional<Item> itemOpt = itemRepository.findById(bookingDtoInput.getItemId());
        if (itemOpt.isEmpty()) {
            throw new ObjectNotFoundException("Item c таким id не существует");
        }
        if (itemOpt.get().getAvailable().equals(false)) {
            throw new ValidationException("Item не доступна для бронирования");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ObjectNotFoundException("User с таким id не зарегистрирован");
        }
        if (bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart())) {
            throw new ValidationException("Конец бронирования не может быть в прошлом");
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
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto setApproved(Long userId, Long bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking c таким id нет в базе"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new ObjectNotFoundException("Item c таким id нет в базе"));
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
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
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
        return BookingMapper.toBookingDto(booking.get());
    }

    @Override
    public List<BookingDto> getBookingsOfUser(Long userId, StateBooking state, Integer from, Integer size) {
        validation(userId, from, size);
        Sort startDesc = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, startDesc);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId, pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(),
                                startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case CURRENT:
                return bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(userId,
                                LocalDateTime.now(), LocalDateTime.now(), startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                                startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, StatusBooking.WAITING, startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, StatusBooking.REJECTED, startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getBookingsAllItems(Long ownerId, StateBooking state, Integer from, Integer size) {
        validation(ownerId, from, size);
        Sort startDesc = Sort.by(Sort.Direction.DESC, "start");
        Pageable pageable = PageRequest.of(from / size, size, startDesc);
        switch (state) {
            case ALL:
                return bookingRepository.findByItem_Owner_Id(ownerId, pageable)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case FUTURE:
                return bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, LocalDateTime.now(), startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case CURRENT:
                return bookingRepository.findByItem_Owner_IdAndEndIsAfterAndStartIsBefore(ownerId, LocalDateTime.now(),
                                LocalDateTime.now(), startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case PAST:
                return bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, LocalDateTime.now(), startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case WAITING:
                return bookingRepository.findByItem_Owner_IdAndStatus(ownerId, StatusBooking.WAITING, startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            case REJECTED:
                return bookingRepository.findByItem_Owner_IdAndStatus(ownerId, StatusBooking.REJECTED, startDesc)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(toList());
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void validation(Long ownerId, Integer from, Integer size) {
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Нет owner c id={}", ownerId);
        }
        if (from < 0) {
            throw new ValidationException("from меньше 0");
        }
        if (size <= 0) {
            throw new ValidationException("size меньше либо равно 0");
        }
    }
}
