//package ru.practicum.shareit.user;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.*;
//
//
//@Repository
//@Slf4j
//public class UserRepositoryImpl implements UserRepository {
//
//    private Long id = 1L;
//    private final Map<Long, User> usersMap = new HashMap<>();
//
//    @Override
//    public List<User> findAll() {
//        return new ArrayList<>(usersMap.values());
//    }
//
//    @Override
//    public User findUserById(Long userId) {
//        return usersMap.get(userId);
//    }
//
//    @Override
//    public User createUser(User user) {
//        user.setId(id);
//        usersMap.put(user.getId(), user);
//        id++;
//        return user;
//    }
//
//    @Override
//    public User updateUser(Long userId, User user) {
//        usersMap.put(userId, user);
//        return user;
//    }
//
//    @Override
//    public void deleteUser(Long userId) {
//        if (usersMap.containsKey(userId)) {
//            usersMap.remove(userId);
//        }
//    }
//}
