package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        @Valid Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDtoUpdate itemDtoUpdate, Long itemId) {
        if (userId == null) {
            throw new ValidationException("нет информации о пользователе, userId = null");
        }
        User owner = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("нет вещи с id = %d", itemId));
        if (!owner.getId().equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException("у пользавателя с id = %d нет вещей", userId);
        }
        if (itemDtoUpdate.getAvailable() != null) {
            item.setAvailable(itemDtoUpdate.getAvailable());
        }
        if (itemDtoUpdate.getDescription() != null) {
            item.setDescription(itemDtoUpdate.getDescription());
        }
        if (itemDtoUpdate.getName() != null) {
            item.setName(itemDtoUpdate.getName());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemInfoDto findItemById(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("недопустимое значение itemId");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(ObjectNotFoundException::new);

        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();

        if (!comments.isEmpty() && comments != null) {
            for (Comment comment : comments) {
                commentsDto.add(CommentMapper.toCommentDto(comment));
            }
        }
        if (bookings.isEmpty() || !item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemInfoDto(item, null, null, commentsDto);
        }
        Booking lastBooking = lastBooking(bookings);
        Booking nextBooking = nextBooking(bookings);
        return ItemMapper.toItemInfoDto(item,
                BookingMapper.toBookingDtoOutputItem(lastBooking),
                BookingMapper.toBookingDtoOutputItem(nextBooking),
                commentsDto);
    }

    @Override
    public List<ItemInfoDto> findItemsOfUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("нет информации о пользователе, userId = null");
        }
        User user = userRepository.findById(userId).orElseThrow(ObjectNotFoundException::new);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemInfoDto> itemsInfoDto = new ArrayList<>();
        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId());
            Booking lastBooking = lastBooking(bookings);
            Booking nextBooking = nextBooking(bookings);
            List<CommentDto> comments = commentRepository.findAllByItemId(item.getId())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(toList());
            itemsInfoDto.add(ItemMapper.toItemInfoDto(item,
                    BookingMapper.toBookingDtoOutputItem(lastBooking),
                    BookingMapper.toBookingDtoOutputItem(nextBooking), comments));
        }
        return itemsInfoDto;
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        List<ItemDto> resultSearch = new ArrayList<>();
        if (text.isEmpty()) {
            return resultSearch;
        }
        List<Item> itemList = itemRepository.search(text);
        for (Item item : itemList) {
            if (item.getAvailable() == true) {
                resultSearch.add(ItemMapper.toItemDto(item));
            }
        }
        return resultSearch;
    }

    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(ValidationException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(ObjectNotFoundException::new);
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(userId, itemId);
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = CommentMapper.toComment(commentDto, item, user, LocalDateTime.now());
                return CommentMapper.toCommentDto(commentRepository.save(comment));
            }
        }
        throw new ValidationException();
    }

    private Booking lastBooking(List<Booking> bookings) {
        Booking lastBooking = null;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                lastBooking = booking;
            }
        }
        return lastBooking;
    }

    private Booking nextBooking(List<Booking> bookings) {
        Booking nextBooking = null;
        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                nextBooking = booking;
            }
        }
        return nextBooking;
    }
}
