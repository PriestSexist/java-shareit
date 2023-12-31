package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemControllerServer {

    private final ItemService itemService;
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto postItem(@RequestBody ItemDto itemDto,
                            @RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод postItem");
        return itemService.postItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable int itemId, @RequestHeader(OWNER_HEADER) int ownerId,
                             @RequestBody ItemDto itemDto) {
        log.debug("Вызван метод patchItem");
        return itemService.patchItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@RequestHeader(OWNER_HEADER) int ownerId,
                                          @PathVariable int itemId) {
        log.debug("Вызван метод getItemById");
        return itemService.getItemById(ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable int itemId) {
        log.debug("Вызван метод deleteItemById");
        itemService.deleteItemById(itemId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllItems(@RequestHeader(OWNER_HEADER) int ownerId,
                                                @RequestParam int from,
                                                @RequestParam int size) {
        log.debug("Вызван метод getAllItems");
        return itemService.getAllItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchedItems(@RequestParam String text,
                                          @RequestParam int from,
                                          @RequestParam int size) {
        log.debug("Вызван метод getSearchedItems");
        return itemService.getSearchedItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(OWNER_HEADER) int ownerId,
                                  @PathVariable int itemId,
                                  @RequestBody CommentDto commentDto) {
        log.debug("Вызван метод postComment");
        return itemService.postComment(ownerId, itemId, commentDto);
    }
}
