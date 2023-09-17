package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public ItemDto postItem(int ownerId, ItemDto itemDto) {
        Item item = ItemMapper.createItem(itemDto);
        item.setOwnerId(ownerId);
        return ItemMapper.createItemDto(itemRepository.postItem(item));
    }

    @Override
    public ItemDto patchItem(int itemId, int ownerId, ItemDto itemDto) {
        Item item = ItemMapper.createItem(itemDto);
        item.setOwnerId(ownerId);
        item.setId(itemId);
        return ItemMapper.createItemDto(itemRepository.patchItem(item));
    }

    @Override
    public ItemDto getItemById(int itemId) {
        return ItemMapper.createItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public void deleteItemById(int itemId) {
        itemRepository.deleteItemById(itemId);
    }

    @Override
    public Collection<ItemDto> getAllItems(int ownerId) {
        return itemRepository.getAllItems(ownerId).stream()
                .map(ItemMapper::createItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getSearchedItems(int ownerId, String text) {
        return itemRepository.getSearchedItems(ownerId, text.toLowerCase()).stream()
                .map(ItemMapper::createItemDto)
                .collect(Collectors.toList());
    }
}
