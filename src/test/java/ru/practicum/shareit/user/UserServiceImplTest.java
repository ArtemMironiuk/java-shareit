package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceImplTest {

    UserServiceImpl userServiceImpl;
    UserRepository userRepository;

    private User user1;
    private UserDto userDto1;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userServiceImpl = new UserServiceImpl();
        userServiceImpl.userRepository = userRepository;
        user1 = new User(1L, "user_1", "user1@email");
        userDto1 = new UserDto(2L, "user_2", "user2@mail");
    }

    @Test
    void testGetAllUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user1));
        List<UserDto> usersDto = userServiceImpl.findAll();

        assertNotNull(usersDto);
        assertEquals(1, usersDto.size());
        assertEquals(user1.getId(), usersDto.get(0).getId());
        assertEquals(user1.getName(), usersDto.get(0).getName());
    }

    @Test
    void testFindUserById() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        UserDto userDto = userServiceImpl.findUserById(1L);

        assertNotNull(userDto);
        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
    }

    @Test
    void testFindUserById404() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("некокектный userId"));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userServiceImpl.findUserById(-1L));

        assertEquals("некокектный userId", exception.getMessage());
    }

    @Test
    void testFindUserById404OfUser() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenThrow(new ObjectNotFoundException("Нет такого user"));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userServiceImpl.findUserById(2L));

        assertEquals("Нет такого user", exception.getMessage());
    }

    @Test
    void testCreateUser() {
        User user = UserMapper.toUser(userDto1);
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDto = userServiceImpl.createUser(userDto1);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void testCreateUser400() {
        UserDto userDto = new UserDto(2L, "user_2", "user2mail");
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenThrow(new ValidationException("неправильный Email"));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userServiceImpl.createUser(userDto));

        assertEquals("неправильный Email", exception.getMessage());
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = new UserDto(1L, "user_21", "user21@mail");
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        when(userRepository.findById(userDto.getId()))
                .thenReturn(Optional.ofNullable(user1));
        UserDto userDto1 = userServiceImpl.updateUser(userDto.getId(), userDto);

        assertNotNull(userDto1);
        assertEquals(user.getId(), userDto1.getId());
        assertEquals(user.getName(), userDto1.getName());
    }

    @Test
    void testUpdateUser404() {
        UserDto userDto = new UserDto(2L, "user_21", "user21@mail");
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        when(userRepository.findById(2L))
                .thenThrow(new ObjectNotFoundException("Нет такого пользователя!"));
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userServiceImpl.updateUser(2L, userDto));

        assertEquals("Нет такого пользователя!", exception.getMessage());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        userServiceImpl.deleteUser(user1.getId());
        UserDto userDto = userServiceImpl.findUserById(1L);

        assertEquals(user1.getName(), userDto.getName());
    }

    @Test
    void testDeleteUser404() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userServiceImpl.deleteUser(user1.getId()));

        assertEquals("Нет такого пользователя!", exception.getMessage());
    }
}
