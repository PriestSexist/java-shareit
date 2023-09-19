package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto postItem(int ownerId, ItemDto itemDto);

    ItemDto patchItem(int itemId, int ownerId, ItemDto itemDto);

    ItemDto getItemById(int itemId);

    void deleteItemById(int itemId);

    Collection<ItemDto> getAllItems(int ownerId);

    Collection<ItemDto> getSearchedItems(int ownerId, String text);
}
