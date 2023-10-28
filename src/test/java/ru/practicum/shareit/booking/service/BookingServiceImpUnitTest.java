package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookingServiceImpUnitTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    BookingService bookingService;
    static final LocalDateTime START = LocalDateTime.of(2024, 10, 20, 22, 21);
    static final LocalDateTime END = LocalDateTime.of(2024, 10, 22, 22, 21);

    @BeforeEach
    void generator() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void postBookingThrowsBookingTimeException() {

        BookingDto bookingDto = new BookingDto(1, 1, END, START, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        Assertions.assertThrows(BookingTimeException.class, () -> bookingService.postBooking(1, bookingDto));
    }

    @Test
    void postBookingThrowsItemNotAvailableForBookingException() {

        BookingDto bookingDto = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.FALSE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Assertions.assertThrows(ItemNotAvailableForBookingException.class, () -> bookingService.postBooking(1, bookingDto));
    }

    @Test
    void postBookingThrowsBookingIdException() {

        BookingDto bookingDto = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Assertions.assertThrows(BookingIdException.class, () -> bookingService.postBooking(1, bookingDto));
    }

    @Test
    void postBooking() {

        BookingDto bookingDtoBeforeWork = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(5, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING));

        BookingDto bookingDtoAfterWork = bookingService.postBooking(5, bookingDtoBeforeWork);

        Assertions.assertEquals(bookingDtoBeforeWork, bookingDtoAfterWork);
    }

    @Test
    void patchBookingThrowsBookingNotFoundException() {

        Mockito.when(bookingRepository.findBookingById(Mockito.anyInt()))
                .thenThrow(new BookingNotFoundException("Booking not found"));

        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService.patchBooking(1, Boolean.TRUE, 1));

    }

    @Test
    void patchBookingThrowsBookingIdException() {

        Mockito.when(bookingRepository.findBookingById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED)));

        Assertions.assertThrows(BookingIdException.class, () -> bookingService.patchBooking(1, Boolean.TRUE, 2));

    }

    @Test
    void patchBookingThrowsBookingStatusException() {

        Mockito.when(bookingRepository.findBookingById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED)));

        Assertions.assertThrows(BookingStatusException.class, () -> bookingService.patchBooking(1, Boolean.TRUE, 3));

    }

    @Test
    void patchBooking() {

        BookingDto bookingDtoBeforeWork = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findBookingById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING)));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED));

        BookingDto bookingDtoAfterWork = bookingService.patchBooking(1, Boolean.TRUE, 3);

        Assertions.assertEquals(bookingDtoBeforeWork, bookingDtoAfterWork);

    }

    @Test
    void getBookingById() {

        BookingDto bookingDtoBeforeWork = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);

        Mockito.when(bookingRepository.findByBookerIdAndIdOrItem_OwnerIdAndId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED)));

        BookingDto bookingDtoAfterWork = bookingService.getBookingById(1, 1);

        Assertions.assertEquals(bookingDtoBeforeWork, bookingDtoAfterWork);
    }

    @Test
    void getItemsThatIBookedAll() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByBookerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getItemsThatIBooked("ALL", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getItemsThatIBookedCurrent() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStartIsBeforeAndEndIsAfterAndBookerIdOrderByStartDesc(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getItemsThatIBooked("CURRENT", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getItemsThatIBookedPast() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByEndIsBeforeAndBookerIdOrderByStartDesc(Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getItemsThatIBooked("PAST", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getItemsThatIBookedFuture() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStartIsAfterAndBookerIdOrderByStartDesc(Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getItemsThatIBooked("FUTURE", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getItemsThatIBookedWaiting() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(Mockito.any(BookingStatus.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getItemsThatIBooked("WAITING", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getItemsThatIBookedRejected() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(Mockito.any(BookingStatus.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getItemsThatIBooked("REJECTED", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getItemsThatIBookedThrowsBookingStateException() {

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Assertions.assertThrows(BookingStateException.class, () -> bookingService.getItemsThatIBooked("ERROR", 1, 0, 5));
    }

    @Test
    void getBookingsOfMyItemsAll() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getBookingsOfMyItems("ALL", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getBookingsOfMyItemsCurrent() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStartIsBeforeAndEndIsAfterAndItem_OwnerIdOrderByStartDesc(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getBookingsOfMyItems("CURRENT", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getBookingsOfMyItemsPast() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByEndIsBeforeAndItem_OwnerIdOrderByStartDesc(Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getBookingsOfMyItems("PAST", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getBookingsOfMyItemsFuture() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStartIsAfterAndItem_OwnerIdOrderByStartDesc(Mockito.any(LocalDateTime.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getBookingsOfMyItems("FUTURE", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getBookingsOfMyItemsWaiting() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStatusAndItem_OwnerIdOrderByStartDesc(Mockito.any(BookingStatus.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getBookingsOfMyItems("WAITING", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getBookingsOfMyItemsRejected() {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<BookingDto> bookingDtoListBeforeWork = new ArrayList<>();
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork1);
        bookingDtoListBeforeWork.add(bookingDtoBeforeWork2);

        Booking bookingBeforeWork1 = new Booking(1, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        Booking bookingBeforeWork2 = new Booking(2, START, END, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingBeforeWork1);
        bookings.add(bookingBeforeWork2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(bookingRepository.findAllByStatusAndItem_OwnerIdOrderByStartDesc(Mockito.any(BookingStatus.class), Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookingDtoListAfterWork = bookingService.getBookingsOfMyItems("REJECTED", 1, 0, 5);

        Assertions.assertEquals(bookingDtoListBeforeWork, bookingDtoListAfterWork);
    }

    @Test
    void getBookingsOfMyItemsThrowBookingStateException() {

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Assertions.assertThrows(BookingStateException.class, () -> bookingService.getBookingsOfMyItems("ERROR", 1, 0, 5));
    }
}