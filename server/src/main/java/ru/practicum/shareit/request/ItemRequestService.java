package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOutput createRequest(Long userId, ItemRequestDtoInput requestDto);

    List<RequestAndResponseDtoOut> findRequestsOfOwner(Long userId);

    List<RequestAndResponseDtoOut> findRequestsOfUser(Long userId, Integer from, Integer size);

    RequestAndResponseDtoOut findRequest(Long userId, Long requestId);
}
