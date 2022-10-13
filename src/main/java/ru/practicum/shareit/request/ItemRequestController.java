package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOutput createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated @RequestBody ItemRequestDtoInput requestDto) {
        log.info("Получен запрос к эндпоинту POST, /requests");
        return itemRequestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestAndResponseDtoOut> findRequestsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту GET, /requests");
        return itemRequestService.findRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public List<RequestAndResponseDtoOut> findRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam (name = "from") Integer from,
                                                             @RequestParam (name = "size") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /requests/all?from={}&size={}", from, size);
        return itemRequestService.findRequestsOfUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestAndResponseDtoOut findRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable(name = "requestId") Long requestId) {
        log.info("Получен запрос к эндпоинту GET, /requests/{requestId}", requestId);
        return itemRequestService.findRequest(userId, requestId);
    }
}
