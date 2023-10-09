package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;

public interface ItemService {
    ItemDto postItem(int ownerId, ItemDto itemDto);

    ItemDto patchItem(int itemId, int ownerId, ItemDto itemDto);

    ItemDtoWithBooking getItemById(int ownerId, int itemId);

    void deleteItemById(int itemId);

    Collection<ItemDtoWithBooking> getAllItems(int ownerId);

    Collection<ItemDto> getSearchedItems(String text);

    CommentDto postComment(int ownerId, int itemId, CommentDto commentDto);
}
