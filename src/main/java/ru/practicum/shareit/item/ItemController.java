package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
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

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int ownerId, @RequestBody ItemDto itemDto) {
        log.debug("Вызван метод patchItem");
        return itemService.patchItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int itemId) {
        log.debug("Вызван метод getItemById");
        return itemService.getItemById(ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable int itemId) {
        log.debug("Вызван метод deleteItemById");
        itemService.deleteItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDtoWithBooking> getAllItems(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.debug("Вызван метод getAllItems");
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getSearchedItems(@RequestParam String text) {
        log.debug("Вызван метод getSearchedItems");
        return itemService.getSearchedItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") int ownerId, @PathVariable int itemId, @RequestBody @Valid CommentDto commentDto) {
        log.debug("Вызван метод postComment");
        return itemService.postComment(ownerId, itemId, commentDto);
    }
}
