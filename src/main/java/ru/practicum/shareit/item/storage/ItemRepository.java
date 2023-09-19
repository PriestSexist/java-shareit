package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Item postItem(Item item);

    Item patchItem(Item item);

    Item getItemById(int itemId);

    void deleteItemById(int itemId);

    Collection<Item> getAllItems(int ownerId);

    Collection<Item> getSearchedItems(int ownerId, String text);
}
