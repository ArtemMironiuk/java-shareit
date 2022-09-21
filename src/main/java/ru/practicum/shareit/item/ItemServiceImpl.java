package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findUserById(userId);
        if (owner == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        @Valid Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDtoUpdate itemDtoUpdate, Long itemId) {
        if (userId == null) {
            throw new ValidationException("нет информации о пользователе, userId = null");
        }
        Item itemInMemory = itemRepository.findItemById(itemId);
        if (itemInMemory == null) {
            throw new  ObjectNotFoundException("нет вещи с id = %d", itemId);
        }
        User owner = userRepository.findUserById(userId);
        if (!owner.equals(itemInMemory.getOwner())) {
            throw new  ObjectNotFoundException("у пользавателя с id = %d нет вещей", userId);
        }
        if (itemDtoUpdate.getAvailable() != null) {
            itemInMemory.setAvailable(itemDtoUpdate.getAvailable());
        }
        if (itemDtoUpdate.getDescription() != null) {
            itemInMemory.setDescription(itemDtoUpdate.getDescription());
        }
        if (itemDtoUpdate.getName() != null) {
            itemInMemory.setName(itemDtoUpdate.getName());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(itemInMemory));
    }

    @Override
    public ItemDto findItemById(Long userId, Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("недопустимое значение itemId");
        }
        return ItemMapper.toItemDto(itemRepository.findItemById(itemId));
    }

    @Override
    public List<ItemDto> findItemsOfUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("нет информации о пользователе, userId = null");
        }
        return itemRepository.findItems()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        List<ItemDto> resultSearch = new ArrayList<>();
        if (text.isEmpty()) {
            return resultSearch;
        }
        List<Item> itemList = itemRepository.findItems();
        for (Item item : itemList) {
            if (item.getAvailable() == true) {
                String nameItem = item.getName().toLowerCase();
                String descriptionItem = item.getDescription().toLowerCase();
                if (nameItem.contains(text.toLowerCase()) || descriptionItem.contains(text.toLowerCase())) {
                    resultSearch.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return resultSearch;
    }
}
