package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private final LocalDateTime start1 = LocalDateTime.of(2022, 10, 22, 22, 21);
    private final LocalDateTime start2 = LocalDateTime.of(2024, 10, 20, 22, 21);

    @Test
    void findLastByItem_OwnerId() {

        LocalDateTime currentTime = LocalDateTime.now();

        User userBeforeWorkItemOwner = new User(1, "Viktor B", "vitekb650@gmail.com");
        User userBeforeWorkItemBooker1 = new User(2, "Kick", "Kick@gmail.com");
        User userBeforeWorkItemBooker2 = new User(3, "Ron", "Ronaldo@gmail.com");
        User userPostedOwner = userRepository.save(userBeforeWorkItemOwner);
        User userPostedBooker1 = userRepository.save(userBeforeWorkItemBooker1);
        User userPostedBooker2 = userRepository.save(userBeforeWorkItemBooker2);

        Item itemForPost1 = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, userPostedOwner);
        Item itemPosted1 = itemRepository.save(itemForPost1);

        Booking bookingForPost1 = new Booking(1, start1, start1.plusDays(3), itemPosted1, userPostedBooker1, BookingStatus.APPROVED);
        Booking bookingForPost2 = new Booking(2, start2, start2.plusDays(3), itemPosted1, userPostedBooker2, BookingStatus.APPROVED);

        Booking bookingPosted1 = bookingRepository.save(bookingForPost1);
        bookingRepository.save(bookingForPost2);

        Booking booking = bookingRepository.findLastByItem_OwnerId(itemPosted1.getId(), userPostedOwner.getId(), BookingStatus.REJECTED, currentTime).stream().findFirst().get();

        Assertions.assertEquals(bookingPosted1, booking);
    }

    @Test
    void findNextByItem_OwnerId() {

        LocalDateTime currentTime = LocalDateTime.now();

        User userBeforeWorkItemOwner = new User(1, "Viktor B", "vitekb650@gmail.com");
        User userBeforeWorkItemBooker1 = new User(2, "Kick", "Kick@gmail.com");
        User userBeforeWorkItemBooker2 = new User(3, "Ron", "Ronaldo@gmail.com");
        User userPostedOwner = userRepository.save(userBeforeWorkItemOwner);
        User userPostedBooker1 = userRepository.save(userBeforeWorkItemBooker1);
        User userPostedBooker2 = userRepository.save(userBeforeWorkItemBooker2);

        Item itemForPost1 = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, userPostedOwner);
        Item itemPosted1 = itemRepository.save(itemForPost1);

        Booking bookingForPost1 = new Booking(1, start1, start1.plusDays(3), itemPosted1, userPostedBooker1, BookingStatus.APPROVED);
        Booking bookingForPost2 = new Booking(2, start2, start2.plusDays(3), itemPosted1, userPostedBooker2, BookingStatus.APPROVED);

        bookingRepository.save(bookingForPost1);
        Booking bookingPosted2 = bookingRepository.save(bookingForPost2);

        Booking booking = bookingRepository.findNextByItem_OwnerId(itemPosted1.getId(), userPostedOwner.getId(), BookingStatus.REJECTED, currentTime).stream().findFirst().get();

        Assertions.assertEquals(bookingPosted2, booking);

    }
}