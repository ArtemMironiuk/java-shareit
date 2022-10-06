package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoOutputItem;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.reducing;
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
        Optional<User> ownerOpt = userRepository.findById(userId);
        if (ownerOpt.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        User owner = ownerOpt.get();
        @Valid Item item = ItemMapper.toItem(itemDto, owner);
        Item item1 = itemRepository.save(item);
        return ItemMapper.toItemDto(item1);
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDtoUpdate itemDtoUpdate, Long itemId) {
        if (userId == null) {
            throw new ValidationException("нет информации о пользователе, userId = null");
        }
        Optional <Item> itemInMemory = itemRepository.findById(itemId);
        if (itemInMemory.isEmpty()) {
            throw new  ObjectNotFoundException("нет вещи с id = %d", itemId);
        }
        Optional <User> ownerOpt = userRepository.findById(userId);
        if (!ownerOpt.get().equals(itemInMemory.get().getOwner())) {
            throw new  ObjectNotFoundException("у пользавателя с id = %d нет вещей", userId);
        }
        if (itemDtoUpdate.getAvailable() != null) {
            itemInMemory.get().setAvailable(itemDtoUpdate.getAvailable());
        }
        if (itemDtoUpdate.getDescription() != null) {
            itemInMemory.get().setDescription(itemDtoUpdate.getDescription());
        }
        if (itemDtoUpdate.getName() != null) {
            itemInMemory.get().setName(itemDtoUpdate.getName());
        }
        return ItemMapper.toItemDto(itemRepository.save(itemInMemory.get()));
    }

    @Override
    public ItemInfoDto findItemById(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("недопустимое значение itemId");
        }
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                commentsDto.add(CommentMapper.toCommentDto(comment));
            }
        }
        if (bookings.isEmpty() || !itemOpt.get().getOwner().getId().equals(userId)) {
            return ItemMapper.toItemInfoDto(itemOpt.get(),null, null,commentsDto);
        }
        Booking lastBooking = null;
        Booking nextBooking = null;
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                lastBooking = booking;
            }
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                nextBooking = booking;
            }
        }
//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<CommentDto> commentsDto = new ArrayList<>();
//        if (!comments.isEmpty()) {
//            for (Comment comment : comments) {
//                commentsDto.add(CommentMapper.toCommentDto(comment));
//            }
//        }
        return ItemMapper.toItemInfoDto(itemOpt.get(),
                BookingMapper.toBookingDtoOutputItem (lastBooking),
                BookingMapper.toBookingDtoOutputItem(nextBooking),
                commentsDto);

//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<CommentDto> commentsDto = new ArrayList<>();
//        if (!comments.isEmpty()) {
//            for (Comment comment : comments) {
//                User author = userRepository.findById(comment.getAuthorId()).get();
//                commentsDto.add(CommentMapper.toCommentDto(comment, author.getName()));
//            }
//            return ResponseEntity.ok (ItemMapper.toItemCommentDto(itemOpt.get(), commentsDto));
//        }
//        return ItemMapper.toItemInfoDto(itemOpt.get(),bookingDtoOutputItems.get(1),bookingDtoOutputItems.get(0));
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
            Booking lastBooking = null;
            Booking nextBooking = null;
            for (Booking booking : bookings) {
                if (booking.getEnd().isBefore(LocalDateTime.now())) {
                    lastBooking = booking;
                }
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    nextBooking = booking;
                }
            }
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

//    @Override
//    public ItemCommentDto findItemByIdWithComments(Long userId, Long itemId) {
//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<CommentDto> commentsDto = new ArrayList<>();
//        for (Comment comment:comments) {
//            User author = userRepository.findById(comment.getAuthorId()).get();
//            commentsDto.add(CommentMapper.toCommentDto(comment, author.getName()));
//        }
//        Item item = itemRepository.findById(itemId).get();
//
//        return ItemMapper.toItemCommentDto(item,commentsDto);
//    }

    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(ValidationException::new);
//        if (user.isEmpty()) {
//            throw new ValidationException("User не зарегистрирован");
//        }
        Item item = itemRepository.findById(itemId).orElseThrow(ObjectNotFoundException::new);
//        if (item.isEmpty()) {
//            throw new ObjectNotFoundException("Item отсутствует");
//        }
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemId(userId,itemId);
        for (Booking booking : bookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = new Comment(commentDto.getId(),commentDto.getText(), item, user, LocalDateTime.now());
                return CommentMapper.toCommentDto(commentRepository.save(comment));
            }
        }
        throw new ValidationException();
    }
}
