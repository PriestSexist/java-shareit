package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private final int itemId;
    @FutureOrPresent(message = "Booking start time should be after current time")
    @NotNull
    private final LocalDateTime start;
    @FutureOrPresent(message = "Booking end time should be after current time")
    @NotNull
    private final LocalDateTime end;
}
