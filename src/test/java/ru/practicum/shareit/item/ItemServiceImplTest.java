package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
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
public class ItemServiceImplTest {

    ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    private User owner1;

    private User user1;

    private Item item;

    private Booking booking1;

    private Booking booking2;

    private Comment comment;

    private ItemDto itemDto1;

    private CommentDto commentDto1;

    private ItemDto itemDtoReq;

    private ItemRequest itemRequest;

    private ItemDtoUpdate itemDtoUpdate;

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    String format = LocalDateTime.now().format(formatter);
    LocalDateTime localDateTime = LocalDateTime.parse(format).withNano(0);

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl();
        itemService.itemRepository = itemRepository;
        itemService.userRepository = userRepository;
        itemService.commentRepository =commentRepository;
        itemService.bookingRepository = bookingRepository;
        itemService.itemRequestRepository = itemRequestRepository;

        owner1 = new User(1L, "owner1", "owner1@mail.ru");
        user1 = new User(2L, "user1", "user1@mail.ru");
        itemDto1 = new ItemDto(1L, "Дрель", "Простая дрель", true, null);
        itemDtoReq = new ItemDto(2L, "Отвертка", "Простая отрвертка", true, 1L);
        itemRequest = new ItemRequest(1L, "Нужна отвертка", user1, localDateTime);
        itemDtoUpdate = new ItemDtoUpdate(1L, "Дрель+", "Аккумуляторная дрель", false);
        item = ItemMapper.toItem(itemDto1, owner1, null);
        booking1 = new Booking(1L, localDateTime.minusDays(2), localDateTime.minusDays(1), item, user1, StatusBooking.APPROVED);
        booking2 = new Booking(2L, localDateTime.plusDays(2), localDateTime.plusDays(3), item, user1, StatusBooking.WAITING);
        comment =new Comment(1L,"Хорошая вещь", item, user1, localDateTime);
        commentDto1 = new CommentDto(1L, "Add comment from user1", "user1", localDateTime);
    }

    @Test
    void testCreateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner1));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Item item = ItemMapper.toItem(itemDtoReq, owner1, itemRequest);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemService.createItem(owner1.getId(),itemDtoReq);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void testCreateItem404User() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.createItem(owner1.getId(),itemDto1));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testCreateItem2Branch() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner1));
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemService.createItem(owner1.getId(),itemDto1);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    void testUpdateItem400UserId() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.updateItem(null, itemDtoUpdate, itemDto1.getId()));
        assertEquals("нет информации о пользователе, userId = null", exception.getMessage());
    }

    @Test
    void testUpdateItem404User() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(owner1.getId(), itemDtoUpdate, itemDto1.getId()));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void testUpdateItem404Item() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(owner1.getId(), itemDtoUpdate, itemDto1.getId()));
        assertEquals("нет вещи с таким id", exception.getMessage());
    }

    @Test
    void testUpdateItem404Owner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(user1.getId(), itemDtoUpdate, itemDto1.getId()));
        assertEquals("у пользавателя с таким id нет вещей", exception.getMessage());
    }

    @Test
    void testUpdateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner1));
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Item item1 = new Item(
                itemDtoUpdate.getId(),
                itemDtoUpdate.getName(),
                itemDtoUpdate.getDescription(),
                itemDtoUpdate.getAvailable(),
                owner1,
                null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item1);
        ItemDto itemDto = itemService.updateItem(owner1.getId(), itemDtoUpdate, itemDto1.getId());
        assertNotNull(itemDto);
        assertEquals(item1.getId(),itemDto.getId());
        assertEquals(item1.getName(), itemDto.getName());
        assertEquals(item1.getDescription(), itemDto.getDescription());
        assertEquals(item1.getAvailable(), itemDto.getAvailable());
    }
    //МОЖНО СДЕЛАТЬ ДЛЯ ВЕТОК ЕЩЕ
    @Test
    void testFindItemById404ItemId() {
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.findItemById(owner1.getId(), null));
        assertEquals("недопустимое значение itemId", exception.getMessage());
    }

    @Test
    void testFindItemById404Item() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.findItemById(owner1.getId(), itemDto1.getId()));
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testFindItemByIdEmptyCommentAndBooking() {
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of());
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of());
        ItemInfoDto itemInfoDto = itemService.findItemById(owner1.getId(),itemDto1.getId());

        assertNotNull(itemInfoDto);
        assertEquals(item.getId(),itemInfoDto.getId());
        assertEquals(item.getName(), itemInfoDto.getName());
        assertEquals(item.getDescription(), itemInfoDto.getDescription());
        assertEquals(item.getAvailable(), itemInfoDto.getAvailable());
    }

    @Test
    void testFindItemById() {
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking1, booking2));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));
        ItemInfoDto itemInfoDto = itemService.findItemById(owner1.getId(),itemDto1.getId());

        assertNotNull(itemInfoDto);
        assertEquals(item.getId(),itemInfoDto.getId());
        assertEquals(item.getName(), itemInfoDto.getName());
        assertEquals(item.getDescription(), itemInfoDto.getDescription());
        assertEquals(item.getAvailable(), itemInfoDto.getAvailable());
        assertEquals(booking1.getId(), itemInfoDto.getLastBooking().getId());
        assertEquals(booking2.getId(), itemInfoDto.getNextBooking().getId());
    }

    @Test
    void testFindItemsOfUser400UserId() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.findItemsOfUser(null, 0, 20));
        assertEquals("нет информации о пользователе, userId = null", exception.getMessage());
    }

    @Test
    void testFindItemsOfUser404User() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.findItemsOfUser(owner1.getId(), 0, 20));
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testFindItemsOfUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner1));
        when(itemRepository.findAllByOwnerIdOrderById(anyLong(), any()))
                .thenReturn(List.of(item));
        when(bookingRepository.findByItemIdOrderByStartDesc(anyLong()))
                .thenReturn(List.of(booking1, booking2));
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));
        List<ItemInfoDto> itemsInfoDto = itemService.findItemsOfUser(owner1.getId(), 0, 20);

        assertNotNull(itemsInfoDto);
        assertEquals(1, itemsInfoDto.size());
    }

    @Test
    void testSearchItemEmpty() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        List<ItemDto> itemsDto = itemService.searchItem(user1.getId(), "", 0, 20);

        assertNotNull(itemsDto);
        assertEquals(0, itemsDto.size());
    }

    @Test
    void testSearchItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(userRepository.save(any()))
                .thenReturn(itemDto1);
        List<ItemDto> itemsDto = itemService.searchItem(user1.getId(), "Дрель", 0, 20);

        assertNotNull(itemsDto);
        assertEquals(0, itemsDto.size());
    }

    @Test
    void testCreateComment400User() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(user1.getId(), commentDto1, itemDto1.getId()));
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testCreateComment404Item() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
//        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.createComment(user1.getId(), commentDto1, itemDto1.getId()));
        assertEquals(null, exception.getMessage());
    }

    @Test
    void testCreateComment() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user1.getId(), itemDto1.getId()))
                .thenReturn(List.of(booking1));
        Comment comment = CommentMapper.toComment(commentDto1, item, user1, localDateTime);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDto = itemService.createComment(user1.getId(), commentDto1, itemDto1.getId());

        assertEquals(commentDto.getText(), commentDto1.getText());
        assertEquals(commentDto.getAuthorName(), user1.getName());
    }

    @Test
    void testCreateComment400() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user1.getId(), itemDto1.getId()))
                .thenReturn(List.of());

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(user1.getId(), commentDto1, itemDto1.getId()));

        assertEquals(null, exception.getMessage());
    }

    @Test
    void testCreateComment400V2() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Item item = ItemMapper.toItem(itemDto1, owner1, null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user1.getId(), itemDto1.getId()))
                .thenReturn(List.of(booking2));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(user1.getId(), commentDto1, itemDto1.getId()));

        assertEquals(null, exception.getMessage());
    }
}
