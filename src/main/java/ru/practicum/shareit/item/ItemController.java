package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader ("X-Sharer-User-Id") Long userId,
                              @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту POST, /items");
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader ("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDtoUpdate itemDtoUpdate,
                              @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту PATCH, /items/{itemId}");
        return itemService.updateItem(userId, itemDtoUpdate, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader ("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос к эндпоинту GET, /items/{itemId}");
        return itemService.findItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> findItemsOfUser(@RequestHeader ("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту GET, /items");
        return itemService.findItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader ("X-Sharer-User-Id") Long userId,
                               @RequestParam String text) {
        log.info("Получен запрос к эндпоинту GET, /items/search?text={}",text);
        return itemService.searchItem(userId, text);
    }
}
