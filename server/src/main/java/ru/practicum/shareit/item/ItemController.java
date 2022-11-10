package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту POST, /items");
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDtoUpdate itemDtoUpdate,
                              @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту PATCH, /items/{itemId}");
        return itemService.updateItem(userId, itemDtoUpdate, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту POST, /items/{itemId}/comment");
        return itemService.createComment(userId, commentDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту GET, /items/{itemId}");
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemInfoDto> findItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /items");
        return itemService.findItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam String text,
                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /items/search?text={}", text);
        return itemService.searchItem(userId, text, from, size);
    }
}
