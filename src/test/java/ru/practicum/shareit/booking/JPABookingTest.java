package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JPABookingTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    BookingRepository bookingRepository;

    @Test
    public void testSave() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String format = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(format).withNano(0);
        User user1 = new User(null, "user1", "user1@mail.ru");
        entityManager.persist(user1);
        User owner = new User(null, "owner1", "owner@mail.ru");
        entityManager.persist(owner);
        Item item2 = new Item(null, "Дрель", "Простая дрель", true, owner, null);
        entityManager.persist(item2);
        Booking booking1 = new Booking(1L, localDateTime.minusDays(1), localDateTime, item2, user1, StatusBooking.WAITING);
        Booking saveBooking1 = bookingRepository.save(booking1);

        assertThat(booking1.getId().equals(saveBooking1.getId()));
        assertThat(saveBooking1.getStart().equals(booking1.getStart()));
        assertThat(saveBooking1.getEnd().equals(booking1.getEnd()));
    }

    @Test
    public void testFindById() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String format = LocalDateTime.now().format(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(format).withNano(0);
        User user1 = new User(null, "user1", "user1@mail.ru");
        entityManager.persist(user1);
        User owner = new User(null, "owner1", "owner@mail.ru");
        entityManager.persist(owner);
        Item item2 = new Item(null, "Дрель", "Простая дрель", true, owner, null);
        entityManager.persist(item2);
        Booking booking1 = new Booking(null, localDateTime.minusDays(1), localDateTime, item2, user1, StatusBooking.WAITING);
        entityManager.persist(booking1);
        Booking result = bookingRepository.findById(booking1.getId()).get();

        assertThat(result).isEqualTo(booking1);
    }
}
