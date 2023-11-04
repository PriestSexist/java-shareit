package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

@Transactional
@DataJpaTest
@TestPropertySource(properties = { "db.name=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    static final LocalDateTime START_1 = LocalDateTime.of(2022, 10, 22, 22, 21);
    static final LocalDateTime START_2 = LocalDateTime.of(2024, 10, 20, 22, 21);

    @Test
    void findLastByItem_OwnerId() {

        User userBeforeWorkItemOwner = new User(1, "Viktor B", "vitekb650@gmail.com");
        User userBeforeWorkItemBooker1 = new User(2, "Kick", "Kick@gmail.com");
        User userBeforeWorkItemBooker2 = new User(3, "Ron", "Ronaldo@gmail.com");
        User userPostedOwner = userRepository.save(userBeforeWorkItemOwner);
        User userPostedBooker1 = userRepository.save(userBeforeWorkItemBooker1);
        User userPostedBooker2 = userRepository.save(userBeforeWorkItemBooker2);

        Item itemForPost1 = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, userPostedOwner);
        Item itemPosted1 = itemRepository.save(itemForPost1);

        Booking bookingForPost1 = new Booking(1, START_1, START_1.plusDays(3), itemPosted1, userPostedBooker1, BookingStatus.APPROVED);
        Booking bookingForPost2 = new Booking(2, START_2, START_2.plusDays(3), itemPosted1, userPostedBooker2, BookingStatus.APPROVED);

        Booking bookingPosted1 = bookingRepository.save(bookingForPost1);
        bookingRepository.save(bookingForPost2);

        Booking booking = bookingRepository.findLastByItem_OwnerId(itemPosted1.getId(), userPostedOwner.getId(), BookingStatus.REJECTED).stream().findFirst().get();

        Assertions.assertEquals(bookingPosted1, booking);
    }

    @Test
    void findNextByItem_OwnerId() {

        User userBeforeWorkItemOwner = new User(1, "Viktor B", "vitekb650@gmail.com");
        User userBeforeWorkItemBooker1 = new User(2, "Kick", "Kick@gmail.com");
        User userBeforeWorkItemBooker2 = new User(3, "Ron", "Ronaldo@gmail.com");
        User userPostedOwner = userRepository.save(userBeforeWorkItemOwner);
        User userPostedBooker1 = userRepository.save(userBeforeWorkItemBooker1);
        User userPostedBooker2 = userRepository.save(userBeforeWorkItemBooker2);

        Item itemForPost1 = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, userPostedOwner);
        Item itemPosted1 = itemRepository.save(itemForPost1);

        Booking bookingForPost1 = new Booking(1, START_1, START_1.plusDays(3), itemPosted1, userPostedBooker1, BookingStatus.APPROVED);
        Booking bookingForPost2 = new Booking(2, START_2, START_2.plusDays(3), itemPosted1, userPostedBooker2, BookingStatus.APPROVED);

        bookingRepository.save(bookingForPost1);
        Booking bookingPosted2 = bookingRepository.save(bookingForPost2);

        Booking booking = bookingRepository.findNextByItem_OwnerId(itemPosted1.getId(), userPostedOwner.getId(), BookingStatus.REJECTED).stream().findFirst().get();

        Assertions.assertEquals(bookingPosted2, booking);

    }
}