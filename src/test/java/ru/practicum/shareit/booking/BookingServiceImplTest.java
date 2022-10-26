package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceImplTest {

    BookingServiceImpl bookingService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    private BookingDto.UserDto booker;
    private BookingDto.ItemDto itemDto1;
    private BookingDto bookingDto;
    private BookingDto bookingDto1;
    private BookingDtoInput bookingDtoInput;
    private BookingDtoInput bookingDtoInputEnd;
    private BookingDtoInput bookingDtoInputBeforeStart;
    private Item item1;
    private Item item2;
    private User owner;
    private User user1;
    private Booking booking1;
    private Booking booking2;

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    String format = LocalDateTime.now().format(formatter);
    LocalDateTime localDateTime = LocalDateTime.parse(format).withNano(0);


    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl();
        bookingService.itemRepository = itemRepository;
        bookingService.userRepository = userRepository;
        bookingService.bookingRepository = bookingRepository;

        owner = new User(2L, "owner1", "owner@mail.ru");
        booker = new BookingDto.UserDto(1L, "user1", "user1@mail.ru");
        itemDto1 = new BookingDto.ItemDto(1L, "Дрель", "Простая дрель", false);
        item1 = new Item(1L, "Дрель", "Простая дрель", false, owner, null);
        item2 = new Item(2L, "Дрель", "Простая дрель", true, owner, null);
        user1 = new User(1L, "user1", "user1@mail.ru");
        bookingDto = new BookingDto(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), itemDto1, booker, StatusBooking.WAITING);
        bookingDto1 = new BookingDto(1L, localDateTime, localDateTime.plusDays(1), itemDto1, booker, StatusBooking.APPROVED);
        bookingDtoInput = new BookingDtoInput(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1));
        bookingDtoInputEnd = new BookingDtoInput(1L, localDateTime, localDateTime.minusDays(1));
        bookingDtoInputBeforeStart = new BookingDtoInput(1L, localDateTime.minusDays(1), localDateTime.plusDays(1));
        booking1 = new Booking(1L, localDateTime.minusDays(2), localDateTime.plusDays(1), item1, user1, StatusBooking.APPROVED);
        booking2 = new Booking(2L, localDateTime.minusDays(1), localDateTime, item2, user1, StatusBooking.WAITING);
    }

    @Test
    void testCreateBooking404Item() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoInput));
        assertEquals("Item c таким id не существует", exception.getMessage());
    }

    @Test
    void testCreateBooking400Item() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDtoInput));
        assertEquals("Item не доступна для бронирования", exception.getMessage());
    }

    @Test
    void testCreateBooking404User() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(user1.getId(), bookingDtoInput));
        assertEquals("User с таким id не зарегистрирован", exception.getMessage());
    }

    @Test
    void testCreateBooking4001() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user1.getId(), bookingDtoInputEnd));
        assertEquals("Конец бронирования не может быть в прошлом", exception.getMessage());
    }

    @Test
    void testCreateBooking4003() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user1.getId(), bookingDtoInputBeforeStart));
        assertEquals("Время начала не может быть в прошлом", exception.getMessage());
    }

    @Test
    void testCreateBooking404Owner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(owner.getId(), bookingDtoInput));
        assertEquals("Владелец вещи не может сам у себя ее забронировать", exception.getMessage());
    }

    @Test
    void testCreateBooking400Booking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking1));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user1.getId(), bookingDtoInput));
        assertEquals("Вещь не доступна к бронированию в это время", exception.getMessage());
    }

    @Test
    void testCreateBooking() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.WAITING);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto bookingDtoNew = bookingService.createBooking(user1.getId(), bookingDtoInput);

        assertNotNull(bookingDtoNew);
        assertEquals(bookingDto.getId(), bookingDtoNew.getId());
        assertEquals(bookingDto.getStart(), bookingDtoNew.getStart());
        assertEquals(bookingDto.getEnd(), bookingDtoNew.getEnd());
        assertEquals(user1.getId(), bookingDtoNew.getBooker().getId());
    }

    @Test
    void testSetApproved404Booking() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.setApproved(user1.getId(), booking.getId(), "true"));
        assertEquals("Booking c таким id нет в базе", exception.getMessage());
    }

    @Test
    void testSetApproved404Item() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.setApproved(user1.getId(), booking.getId(), "true"));
        assertEquals("Item c таким id нет в базе", exception.getMessage());
    }

    @Test
    void testSetApproved404ItemOwner() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.setApproved(user1.getId(), booking.getId(), "true"));
        assertEquals("У Item другой владелец", exception.getMessage());
    }

    @Test
    void testSetApproved400ApproveTrue() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.APPROVED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.setApproved(owner.getId(), booking.getId(), "true"));
        assertEquals("Бронирование уже было подтверждено", exception.getMessage());
    }

    @Test
    void testSetApproved400ApproveFalse() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.REJECTED);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.setApproved(owner.getId(), booking.getId(), "false"));
        assertEquals("Бронирование уже было отклонено", exception.getMessage());
    }

    @Test
    void testSetApprovedTrue() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDtoNew = bookingService.setApproved(owner.getId(), booking.getId(), "true");

        assertNotNull(bookingDtoNew);
        assertEquals(booking.getId(), bookingDtoNew.getId());
        assertEquals(booking.getStart(), bookingDtoNew.getStart());
        assertEquals(booking.getEnd(), bookingDtoNew.getEnd());
        assertEquals(String.valueOf(booking.getStatus()), String.valueOf(bookingDtoNew.getStatus()));
    }

    @Test
    void testSetApprovedFalse() {
        Booking booking = new Booking(1L, localDateTime.plusMinutes(10), localDateTime.plusDays(1), item1, user1,
                StatusBooking.WAITING);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto bookingDtoNew = bookingService.setApproved(owner.getId(), booking.getId(), "false");

        assertNotNull(bookingDtoNew);
        assertEquals(booking.getId(), bookingDtoNew.getId());
        assertEquals(booking.getStart(), bookingDtoNew.getStart());
        assertEquals(booking.getEnd(), bookingDtoNew.getEnd());
        assertEquals(String.valueOf(booking.getStatus()), String.valueOf(bookingDtoNew.getStatus()));
    }
}
