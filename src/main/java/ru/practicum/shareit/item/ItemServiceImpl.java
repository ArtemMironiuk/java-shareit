package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
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
        if (!userId.equals(itemOpt.get().getOwner().getId())) {
            return ItemMapper.toItemInfoDto(itemOpt.get(),null,null);
        }
        if (bookings.isEmpty()) {
            return ItemMapper.toItemInfoDto(itemOpt.get(),null,null);
        }
//        List<Comment> comments = commentRepository.findAllByItemId(itemId);
//        List<CommentDto> commentsDto = new ArrayList<>();
//        if (!comments.isEmpty()) {
//            for (Comment comment : comments) {
//                User author = userRepository.findById(comment.getAuthorId()).get();
//                commentsDto.add(CommentMapper.toCommentDto(comment, author.getName()));
//            }
//            return ResponseEntity.ok (ItemMapper.toItemCommentDto(itemOpt.get(), commentsDto));
//        }
        return ItemMapper.toItemInfoDto(itemOpt.get(),bookings.get(0),bookings.get(1));
    }

    @Override
    public List<ItemDto> findItemsOfUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("нет информации о пользователе, userId = null");
        }
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ValidationException("User не зарегистрирован");
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Item отсутствует");
        }
        Comment comment = new Comment(commentDto.getId(),commentDto.getText(), itemId, userId, LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment),user.get().getName());
    }
}
