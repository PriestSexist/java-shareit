package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongIdException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Integer, Item> items = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final UserRepository userRepository;

    @Override
    public Item postItem(Item item) {

        if (userRepository.getUserById(item.getOwnerId()) == null) {
            throw new UserNotFoundException("User not found");
        }

        item.setId(counter.incrementAndGet());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item patchItem(Item item) {

        Item itemForReplace = items.get(item.getId());

        if (item.getOwnerId() != itemForReplace.getOwnerId()) {
            throw new WrongIdException("Owner ids are different");
        }

        if (item.getName() != null) {
            itemForReplace.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemForReplace.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemForReplace.setAvailable(item.getAvailable());
        }

        items.replace(itemForReplace.getId(), itemForReplace);
        return itemForReplace;
    }

    @Override
    public Item getItemById(int itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Item not found");
        }
        return items.get(itemId);
    }

    @Override
    public void deleteItemById(int itemId) {
        if (items.remove(itemId) == null) {
            throw new ItemNotFoundException("Item not found");
        }
    }

    @Override
    public Collection<Item> getAllItems(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getSearchedItems(int ownerId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text) || item.getName().toLowerCase().contains(text))
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
