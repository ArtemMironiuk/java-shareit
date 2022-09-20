package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.ConflictException;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto findUserById(Long userId) {
        validationId(userId);
        return UserMapper.toUserDto(userRepository.findUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        @Valid User user = UserMapper.toUser(userDto);
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("неправильный Email");
        }
        validDuplicate(userDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        validationId(userId);
        User user = userRepository.findUserById(userId);
        if (user != null) {
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                validDuplicate(userDto);
                user.setEmail(userDto.getEmail());
            }
            return UserMapper.toUserDto(userRepository.updateUser(user.getId(), user));

        }
        throw new ObjectNotFoundException("нет пользователя с таким id");
    }

    @Override
    public void deleteUser(Long userId) {
        validationId(userId);
        userRepository.deleteUser(userId);
    }

    private void validDuplicate(UserDto userDto) {
        List<User> users = userRepository.findAll();
        for (User user1 : users) {
            if (user1.getEmail().contains(userDto.getEmail())) {
                throw new ConflictException("пользователь с таким email уже существует");
            }
        }
    }

    private void validationId(Long id) {
        if (id <= 0) {
            throw new ObjectNotFoundException("некокектный userId");
        }
    }
}
