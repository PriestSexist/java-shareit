package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private final LocalDateTime start = LocalDateTime.of(2024, 10, 20, 22, 21);
    private final LocalDateTime end = LocalDateTime.of(2024, 10, 22, 22, 21);

    @Test
    void postBooking() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost);

        BookingDto bookingDtoForPost = new BookingDto(1, itemDtoPosted.getId(), start, end, ItemMapper.createItem(itemDtoPosted, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoPosted = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDtoPosted.getId()).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingDtoPosted.getId()));
        assertThat(booking.getStart(), equalTo(bookingDtoPosted.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDtoPosted.getEnd()));
        assertThat(booking.getStatus(), equalTo(bookingDtoPosted.getStatus()));
        assertThat(booking.getItem(), equalTo(bookingDtoPosted.getItem()));
        assertThat(booking.getBooker(), equalTo(bookingDtoPosted.getBooker()));

    }


    @Test
    void patchBooking() {
        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost);

        BookingDto bookingDtoForPost = new BookingDto(1, itemDtoPosted.getId(), start, end, ItemMapper.createItem(itemDtoPosted, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoPosted = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost);
        BookingDto bookingDtoPatched = bookingService.patchBooking(bookingDtoPosted.getId(), Boolean.TRUE, userDtoPostedOwner.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDtoPatched.getId()).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingDtoPatched.getId()));
        assertThat(booking.getStart(), equalTo(bookingDtoPatched.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDtoPatched.getEnd()));
        assertThat(booking.getStatus(), equalTo(bookingDtoPatched.getStatus()));
        assertThat(booking.getItem(), equalTo(bookingDtoPatched.getItem()));
        assertThat(booking.getBooker(), equalTo(bookingDtoPatched.getBooker()));
    }

    @Test
    void getBookingById() {
        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost);

        BookingDto bookingDtoForPost = new BookingDto(1, itemDtoPosted.getId(), start, end, ItemMapper.createItem(itemDtoPosted, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoPosted = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost);
        BookingDto bookingDtoGot = bookingService.getBookingById(bookingDtoPosted.getId(), userDtoPostedBooker.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDtoPosted.getId()).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingDtoGot.getId()));
        assertThat(booking.getStart(), equalTo(bookingDtoGot.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDtoGot.getEnd()));
        assertThat(booking.getStatus(), equalTo(bookingDtoGot.getStatus()));
        assertThat(booking.getItem(), equalTo(bookingDtoGot.getItem()));
        assertThat(booking.getBooker(), equalTo(bookingDtoGot.getBooker()));
    }

    @Test
    void getItemsThatIBookedAll() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getItemsThatIBooked("ALL", userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getItemsThatIBookedCurrent() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getItemsThatIBooked("CURRENT", userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getItemsThatIBookedPast() {

        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = start.plusDays(3);

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getItemsThatIBooked("PAST", userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getItemsThatIBookedFuture() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getItemsThatIBooked("FUTURE", userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getItemsThatIBookedWaiting() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getItemsThatIBooked("WAITING", userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getItemsThatIBookedRejected() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        BookingDto bookingDtoPosted1 = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        BookingDto bookingDtoPosted2 = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);
        bookingService.patchBooking(bookingDtoPosted1.getId(), Boolean.FALSE, userDtoPostedOwner.getId());
        bookingService.patchBooking(bookingDtoPosted2.getId(), Boolean.FALSE, userDtoPostedOwner.getId());

        List<BookingDto> bookingDtoListGot = bookingService.getItemsThatIBooked("REJECTED", userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getBookingsOfMyItemsAll() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getBookingsOfMyItems("ALL", userDtoPostedOwner.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getBookingsOfMyItemsCurrent() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getBookingsOfMyItems("CURRENT", userDtoPostedOwner.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getBookingsOfMyItemsPast() {

        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = start.plusDays(3);

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getBookingsOfMyItems("PAST", userDtoPostedOwner.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getBookingsOfMyItemsFuture() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getBookingsOfMyItems("FUTURE", userDtoPostedOwner.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getBookingsOfMyItemsWaiting() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);

        List<BookingDto> bookingDtoListGot = bookingService.getBookingsOfMyItems("WAITING", userDtoPostedOwner.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }

    @Test
    void getBookingsOfMyItemsRejected() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted1 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost1);
        ItemDto itemDtoPosted2 = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost2);

        BookingDto bookingDtoForPost1 = new BookingDto(1, itemDtoPosted1.getId(), start, end, ItemMapper.createItem(itemDtoPosted1, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoForPost2 = new BookingDto(1, itemDtoPosted2.getId(), start, end, ItemMapper.createItem(itemDtoPosted2, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);

        BookingDto bookingDtoPosted1 = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost1);
        BookingDto bookingDtoPosted2 = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost2);
        bookingService.patchBooking(bookingDtoPosted1.getId(), Boolean.FALSE, userDtoPostedOwner.getId());
        bookingService.patchBooking(bookingDtoPosted2.getId(), Boolean.FALSE, userDtoPostedOwner.getId());

        List<BookingDto> bookingDtoListGot = bookingService.getBookingsOfMyItems("REJECTED", userDtoPostedOwner.getId(), 0, 5);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b ", Booking.class);
        List<BookingDto> bookingDtoList = query.getResultList().stream()
                .map(BookingMapper::createBookingDto)
                .collect(Collectors.toList());

        Assertions.assertEquals(bookingDtoListGot, bookingDtoList);

    }
}