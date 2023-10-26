package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.ShortBooking;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoWithBooking {
    private final int id;
    @NotBlank(message = "name can't be blank")
    private final String name;
    @NotBlank(message = "description can't be blank")
    private final String description;
    @NotNull(message = "available can't be null")
    private final Boolean available;
    private final int ownerId;
    private final ShortBooking lastBooking;
    private final ShortBooking nextBooking;
    private final List<CommentDto> comments;
}
