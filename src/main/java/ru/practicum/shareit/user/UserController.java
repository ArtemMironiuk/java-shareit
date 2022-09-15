package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен запрос к эндпоинту GET, http://localhost:8080/users");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById (@PathVariable Long userId) {
        log.info("Получен запрос к эндпоинту GET, http://localhost:8080/users/{userId}");
        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser (@RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту POST, http://localhost:8080/users");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser (@PathVariable Long userId,
                            @RequestBody UserDto userDto) {
        log.info("Получен запрос к эндпоинту PATCH, http://localhost:8080/users/{userId}");
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser (@PathVariable Long userId) {
        log.info("Получен запрос к эндпоинту DELETE, http://localhost:8080/users/{userId}");
        userService.deleteUser(userId);
    }
}
