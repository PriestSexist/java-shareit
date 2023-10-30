package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BookingDto {
    private final int id;
    private final int itemId;
    @FutureOrPresent(message = "Booking start time should be after current time")
    @NotNull
    private final LocalDateTime start;
    @FutureOrPresent(message = "Booking end time should be after current time")
    @NotNull
    private final LocalDateTime end;
    private final Item item;
    private final User booker;
    private final BookingStatus status;
}
