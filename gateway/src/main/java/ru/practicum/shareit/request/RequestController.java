package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid ItemRequestDtoInput requestDto) {
        log.info("Получен запрос к эндпоинту POST, /requests");
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту GET, /requests");
        return requestClient.findRequestsOfOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam (name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос к эндпоинту GET, /requests/all?from={}&size={}", from, size);
        return requestClient.findRequestsOfUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable(name = "requestId") Long requestId) {
        log.info("Получен запрос к эндпоинту GET, /requests/{requestId}", requestId);
        return requestClient.findRequest(userId, requestId);
    }
}