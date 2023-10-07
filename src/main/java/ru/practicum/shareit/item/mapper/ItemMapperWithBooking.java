package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.ShortBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public final class ItemMapperWithBooking {

    public static ItemDtoWithBooking createItemDtoWithBooking(Item item, ShortBooking lastBooking, ShortBooking nextBooking, List<CommentDto> comments) {
        return ItemDtoWithBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
