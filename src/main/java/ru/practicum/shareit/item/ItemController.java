package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto postItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.debug("Вызван метод postItem");
        return itemService.postItem(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto, @PathVariable int id, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.debug("Вызван метод patchItem");
        return itemService.patchItem(id, ownerId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable int id) {
        log.debug("Вызван метод getItemById");
        return itemService.getItemById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable int id) {
        log.debug("Вызван метод deleteItemById");
        itemService.deleteItemById(id);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.debug("Вызван метод getAllItems");
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchedItems(@RequestHeader("X-Sharer-User-Id") int ownerId, @RequestParam String text) {
        log.debug("Вызван метод getSearchedItems");
        return itemService.getSearchedItems(ownerId, text);
    }
}
