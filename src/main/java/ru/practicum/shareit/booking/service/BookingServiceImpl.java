package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto postBooking(int ownerId, BookingDto bookingDto) {

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BookingTimeException("Error with booking time");
        }

        User userFromDb = userRepository.findUserById(ownerId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });

        Item itemFromDb = itemRepository.findItemById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new ItemNotFoundException("Item not found");
        });


        if (!itemFromDb.getAvailable()) {
            throw new ItemNotAvailableForBookingException("Item not available for booking due to available = false");
        }

        if (userFromDb.getId() == itemFromDb.getOwner().getId()) {
            throw new BookingIdException("Item not available for booking due to you can't book your own item");
        }

        Booking booking = BookingMapper.createBooking(bookingDto, itemFromDb, userFromDb);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.createBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto patchBooking(int bookingId, Boolean approved, int ownerId) {

        Booking bookingFromDb = bookingRepository.findBookingById(bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Booking not found");
        });

        if (bookingFromDb.getItem().getOwner().getId() != ownerId) {
            throw new BookingIdException("You don't have access for this booking");
        }

        if (bookingFromDb.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingStatusException("You already approved booking");
        }

        bookingFromDb.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.createBookingDto(bookingRepository.save(bookingFromDb));
    }

    @Override
    public BookingDto getBookingById(int bookingId, int ownerId) {

        Booking bookingFromDb = bookingRepository.findByBookerIdAndIdOrItem_OwnerIdAndId(ownerId, bookingId, ownerId, bookingId).orElseThrow(() -> {
            throw new BookingNotFoundException("Booking not found");
        });

        return BookingMapper.createBookingDto(bookingFromDb);
    }

    @Override
    public Collection<BookingDto> getItemsThatIBooked(String state, int ownerId) {

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        List<Booking> bookingList;

        userRepository.findUserById(ownerId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByBookerId(ownerId);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByStartIsBeforeAndEndIsAfterAndBookerId(localDateTimeNow, localDateTimeNow, ownerId);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByEndIsBeforeAndBookerId(localDateTimeNow, ownerId);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByStartIsAfterAndBookerId(localDateTimeNow, ownerId);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByStatusAndBookerId(BookingStatus.WAITING, ownerId);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByStatusAndBookerId(BookingStatus.REJECTED, ownerId);
                break;
            default:
                throw new BookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingList.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::createBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getBookingsOfMyItems(String state, int ownerId) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        List<Booking> bookingList;

        userRepository.findUserById(ownerId).orElseThrow(() -> {
            throw new UserNotFoundException("User not found");
        });

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByItem_OwnerId(ownerId);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByStartIsBeforeAndEndIsAfterAndItem_OwnerId(localDateTimeNow, localDateTimeNow, ownerId);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByEndIsBeforeAndItem_OwnerId(localDateTimeNow, ownerId);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByStartIsAfterAndItem_OwnerId(localDateTimeNow, ownerId);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByStatusAndItem_OwnerId(BookingStatus.WAITING, ownerId);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByStatusAndItem_OwnerId(BookingStatus.REJECTED, ownerId);
                break;
            default:
                throw new BookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::createBookingDto).collect(Collectors.toList());
    }


}
