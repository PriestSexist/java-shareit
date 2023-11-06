package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.ShortBooking;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoWithBooking {
    private final int id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final int ownerId;
    private final ShortBooking lastBooking;
    private final ShortBooking nextBooking;
    private final List<CommentDto> comments;
}
