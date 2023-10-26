package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    ItemDto postItem(int ownerId, ItemDto itemDto);

    ItemDto patchItem(int itemId, int ownerId, ItemDto itemDto);

    ItemDtoWithBooking getItemById(int ownerId, int itemId);

    void deleteItemById(int itemId);

    List<ItemDtoWithBooking> getAllItems(int ownerId, int from, int size);

    List<ItemDto> getSearchedItems(String text, int from, int size);

    CommentDto postComment(int ownerId, int itemId, CommentDto commentDto);
}
