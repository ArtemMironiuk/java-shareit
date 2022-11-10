package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JPAUserTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @Test
    public void testFindAllIsEmpty() {
        List<User> users = userRepository.findAll();

        assertThat(users).isEmpty();
    }

    @Test
    public void testSave() {
        User user1 = new User(1L, "user1", "user1@mail.ru");
        User saveUser1 = userRepository.save(user1);

        assertThat(user1.getId().equals(saveUser1.getId()));
        assertThat(saveUser1.getName().equals(user1.getName()));
        assertThat(saveUser1.getEmail().equals(user1.getEmail()));
    }

    @Test
    public void testFindId() {
        User user1 = new User(null, "user1", "user1@mail.ru");
        entityManager.persist(user1);
        User user2 = new User(null, "user2", "user2@mail.ru");
        entityManager.persist(user2);

        User foundUser = userRepository.findById(user2.getId()).get();

        assertThat(foundUser).isEqualTo(user2);
    }

    @Test
    public void testDeleteId() {
        User user1 = new User(null, "user1", "user1@mail.ru");
        entityManager.persist(user1);
        User user2 = new User(null, "user2", "user2@mail.ru");
        entityManager.persist(user2);

        userRepository.deleteById(user1.getId());
        List<User> users = userRepository.findAll();

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getId().equals(user2.getId()));
    }
}
