package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту POST, /items");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDtoUpdate itemDtoUpdate,
                                             @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту PATCH, /items/{itemId}");
        return itemClient.updateItem(userId, itemDtoUpdate, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @Positive @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту POST, /items/{itemId}/comment");
        return itemClient.createComment(userId, commentDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @Positive @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту GET, /items/{itemId}");
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /items");
        return itemClient.findItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "text") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /items/search?text={}", text);
        return itemClient.searchItem(userId, text, from, size);
    }
}
