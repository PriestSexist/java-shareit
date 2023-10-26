package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.ShortBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public final class BookingMapper {
    public static Booking createBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto createBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static ShortBooking createShortBooking(Booking booking) {
        if (booking == null) {
            return null;
        }
        return ShortBooking.builder().id(booking.getId()).bookerId(booking.getBooker().getId()).build();
    }
}
