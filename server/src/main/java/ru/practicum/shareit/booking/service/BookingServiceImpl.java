package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public BookingDto postBooking(int ownerId, BookingDto bookingDto) {

        User userFromDb = userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Item itemFromDb = itemRepository.findItemById(bookingDto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));


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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public BookingDto patchBooking(int bookingId, Boolean approved, int ownerId) {

        Booking bookingFromDb = bookingRepository.findBookingById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (bookingFromDb.getItem().getOwner().getId() != ownerId) {
            throw new BookingIdException("You don't have access for this booking");
        }

        if (bookingFromDb.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingStatusException("You already approved booking");
        }

        bookingFromDb.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.createBookingDto(bookingRepository.save(bookingFromDb));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public BookingDto getBookingById(int bookingId, int ownerId) {

        Booking bookingFromDb = bookingRepository.findByBookerIdAndIdOrItem_OwnerIdAndId(ownerId, bookingId, ownerId, bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        return BookingMapper.createBookingDto(bookingFromDb);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<BookingDto> getItemsThatIBooked(String state, int ownerId, int from, int size) {

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        Page<Booking> bookingList;

        userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByStartIsBeforeAndEndIsAfterAndBookerIdOrderByStartDesc(localDateTimeNow, localDateTimeNow, ownerId, pageRequest);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByEndIsBeforeAndBookerIdOrderByStartDesc(localDateTimeNow, ownerId, pageRequest);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByStartIsAfterAndBookerIdOrderByStartDesc(localDateTimeNow, ownerId, pageRequest);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(BookingStatus.WAITING, ownerId, pageRequest);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByStatusAndBookerIdOrderByStartDesc(BookingStatus.REJECTED, ownerId, pageRequest);
                break;
            default:
                throw new BookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingList.map(BookingMapper::createBookingDto).getContent();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<BookingDto> getBookingsOfMyItems(String state, int ownerId, int from, int size) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        Page<Booking> bookingList;

        userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (state) {
            case "ALL":
                bookingList = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(ownerId, pageRequest);
                break;
            case "CURRENT":
                bookingList = bookingRepository.findAllByStartIsBeforeAndEndIsAfterAndItem_OwnerIdOrderByStartDesc(localDateTimeNow, localDateTimeNow, ownerId, pageRequest);
                break;
            case "PAST":
                bookingList = bookingRepository.findAllByEndIsBeforeAndItem_OwnerIdOrderByStartDesc(localDateTimeNow, ownerId, pageRequest);
                break;
            case "FUTURE":
                bookingList = bookingRepository.findAllByStartIsAfterAndItem_OwnerIdOrderByStartDesc(localDateTimeNow, ownerId, pageRequest);
                break;
            case "WAITING":
                bookingList = bookingRepository.findAllByStatusAndItem_OwnerIdOrderByStartDesc(BookingStatus.WAITING, ownerId, pageRequest);
                break;
            case "REJECTED":
                bookingList = bookingRepository.findAllByStatusAndItem_OwnerIdOrderByStartDesc(BookingStatus.REJECTED, ownerId, pageRequest);
                break;
            default:
                throw new BookingStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.map(BookingMapper::createBookingDto).getContent();

    }
}
