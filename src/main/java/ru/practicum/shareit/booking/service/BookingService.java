package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto postBooking(int ownerId, BookingDto bookingDto);

    BookingDto patchBooking(int bookingId, Boolean approved, int ownerId);

    BookingDto getBookingById(int bookingId, int ownerId);

    Collection<BookingDto> getItemsThatIBooked(String state, int ownerId);

    Collection<BookingDto> getBookingsOfMyItems(String state, int ownerId);
}
