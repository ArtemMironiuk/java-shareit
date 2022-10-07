package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Нет такого user");
        }
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        @Valid User user = UserMapper.toUser(userDto);
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("неправильный Email");
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        validationId(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Нет такого пользователя!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        validationId(userId);
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Нет такого пользователя!"));
        userRepository.deleteById(userId);
    }

    private void validationId(Long id) {
        if (id <= 0) {
            throw new ObjectNotFoundException("некокектный userId");
        }
    }
}
