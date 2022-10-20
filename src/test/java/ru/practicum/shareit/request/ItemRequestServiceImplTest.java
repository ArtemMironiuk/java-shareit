package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceImplTest {

    ItemRequestServiceImpl itemRequestService;
    @MockBean
    ItemRequestRepository requestRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;

    private ItemRequestDtoInput itemRequestDtoInput;

    private ItemRequestDtoOutput itemRequestDtoOutput;

    private RequestAndResponseDtoOut requestAndResponseDtoOut;

    private RequestAndResponseDtoOut.ItemDto itemDto;

    private User user;
    private User user2;

    @BeforeEach
    void beforeEach() {
        requestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl();
        itemRequestService.itemRequestRepository = requestRepository;
        itemRequestService.userRepository = userRepository;
        itemRequestService.itemRepository = itemRepository;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String format = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(format);
        user = new User(1L, "user1", "user1@mail.ru");
        user2 = new User(2L, "user2", "user2@mail.ru");
        itemDto = new RequestAndResponseDtoOut.ItemDto(1L, "item1", "items", true, 1L, 1L);
        itemRequestDtoInput = new ItemRequestDtoInput("Описание запроса");
        itemRequestDtoOutput = new ItemRequestDtoOutput(1L, "Описание запроса", localDateTime);
        requestAndResponseDtoOut = new RequestAndResponseDtoOut(1L, "Описание запроса", localDateTime, List.of(itemDto));
    }

    @Test
    void testCreateRequest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoInput, user);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDtoOutput itemRequestDtoOutput1 = itemRequestService.createRequest(user.getId(), itemRequestDtoInput);
        assertNotNull(itemRequestDtoOutput1);
        assertEquals(itemRequest.getDescription(), itemRequestDtoOutput1.getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                itemRequestDtoOutput1.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertEquals(user.getId(), itemRequest.getRequester().getId());
        assertEquals(user.getName(), itemRequest.getRequester().getName());
    }

    @Test
    void testCreateRequest404() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("User не зарегистрирован"));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.createRequest(user.getId(), itemRequestDtoInput));

        assertEquals("User не зарегистрирован", exception.getMessage());
    }

    @Test
    void testFindRequestsOfOwner() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoInput, user2);
        Item item = new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findByRequest_Id(anyLong()))
                .thenReturn(List.of(item));
        when(requestRepository.findByRequesterId(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        List<RequestAndResponseDtoOut> requestAndResponsesDtoOut = itemRequestService.findRequestsOfOwner(user2.getId());
        assertNotNull(requestAndResponsesDtoOut);
        assertEquals(1, requestAndResponsesDtoOut.size());
        assertEquals(itemRequest.getDescription(), requestAndResponsesDtoOut.get(0).getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                requestAndResponsesDtoOut.get(0).getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testFindRequestsOfOwner404() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("User не зарегистрирован"));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.findRequestsOfOwner(user2.getId()));

        assertEquals("User не зарегистрирован", exception.getMessage());
    }

    @Test
    void testFindRequestsOfUser() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoInput, user2);
        Item item = new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
        Pageable pageable = PageRequest.ofSize(20);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> items = new PageImpl<>(itemRequests, pageable, itemRequests.size());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByRequest_Id(anyLong()))
                .thenReturn(List.of(item));
        when(requestRepository.findAll(any(Pageable.class)))
                .thenReturn(items);
        List<RequestAndResponseDtoOut> requestAndResponsesDtoOut = itemRequestService.findRequestsOfUser(user.getId(), 0, 20);
        assertNotNull(requestAndResponsesDtoOut);
        assertEquals(1, requestAndResponsesDtoOut.size());
    }

    @Test
    void testFindRequestsOfUserEmpty() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        List<RequestAndResponseDtoOut> requestAndResponsesDtoOut = itemRequestService.findRequestsOfUser(user.getId(), null, null);
        assertNotNull(requestAndResponsesDtoOut);
        assertEquals(0, requestAndResponsesDtoOut.size());
    }

    @Test
    void testFindRequestsOfUser400from() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.findRequestsOfUser(user.getId(), -1, 20));
        assertEquals("from меньше 0", exception.getMessage());
    }

    @Test
    void testFindRequestsOfUser400size() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.findRequestsOfUser(user.getId(), 0, 0));
        assertEquals("size меньше либо равно 0", exception.getMessage());
    }

    @Test
    void testFindRequest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoInput, user2);
        Item item = new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByRequest_Id(anyLong()))
                .thenReturn(List.of(item));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        RequestAndResponseDtoOut requestAndResponseDtoOut = itemRequestService.findRequest(user.getId(), 1L);
        assertNotNull(requestAndResponseDtoOut);
        assertEquals(itemRequest.getDescription(), requestAndResponseDtoOut.getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                requestAndResponseDtoOut.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testFindRequest404User() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("User не зарегистрирован"));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.findRequest(user.getId(), 1L));
        assertEquals("User не зарегистрирован", exception.getMessage());
    }

//    @Test
//    void testFindRequest404() {
//        when(userRepository.findById(anyLong()))
//                .thenReturn(Optional.of(user));
//        when(requestRepository.findById(anyLong()))
//                .thenThrow(new ObjectNotFoundException("Ничего не найдено по requestId = {}", 1L));
//        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
//                () -> itemRequestService.findRequest(user.getId(), 1L));
//        assertEquals("Ничего не найдено по requestId = 1", exception.getMessage());
//    }
}
