package ru.practicum.shareit.request;


import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemRequestMapper {

    public static ItemRequestDtoOutput toItemRequestDtoOutput(ItemRequest itemRequest) {
        return ItemRequestDtoOutput.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDtoInput requestDto, User user) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
    }

    public static RequestAndResponseDtoOut toRequestAndResponseDtoOut(ItemRequest request, List<Item> itemList) {
        List<RequestAndResponseDtoOut.ItemDto> itemDtoList = itemList.stream()
                .map(item -> new RequestAndResponseDtoOut.ItemDto(item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        item.getRequest().getId(),
                        item.getOwner().getId()))
                .collect(toList());
        return RequestAndResponseDtoOut.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemDtoList)
                .build();
    }
}
