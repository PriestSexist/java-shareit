package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private final int bookingId;
    private final int ownerId;
    private final int bookerId;
    private final int itemId;
    private final LocalDateTime dateOfBooking;
    private final int duration; // в днях
    private final LocalDateTime dateOfEndingBooking;
}
