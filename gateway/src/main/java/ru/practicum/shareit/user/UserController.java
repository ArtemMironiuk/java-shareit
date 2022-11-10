package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Получен запрос к эндпоинту GET, /users");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable Long userId) {
        log.info("Получен запрос к эндпоинту GET, /users/{userId}");
        return userClient.findUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated({Create.class}) UserDto userDto) {
        log.info("Получен запрос к эндпоинту POST, /users");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive Long userId,
                              @RequestBody @Valid UserDto userDto) {
        log.info("Получен запрос к эндпоинту PATCH, /users/{userId}");
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long userId) {
        log.info("Получен запрос к эндпоинту DELETE, /users/{userId}");
        return userClient.deleteUser(userId);
    }
}
