package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postItem(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                           @RequestHeader(OWNER_HEADER) int ownerId) {
        log.info("Creating item {}, userId={}", itemRequestDto, ownerId);
        return itemClient.postItem(ownerId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable int itemId,
                                            @RequestHeader(OWNER_HEADER) int ownerId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Updating item {}, userId={}, itemId={}", itemRequestDto, ownerId, itemId);
        return itemClient.patchItem(itemId, ownerId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(OWNER_HEADER) int ownerId,
                                              @PathVariable int itemId) {
        log.info("Getting userId={}, itemId={}", ownerId, itemId);
        return itemClient.getItemById(ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable int itemId) {
        log.info("Deleting itemId={}", itemId);
        itemClient.deleteItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(OWNER_HEADER) int ownerId,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Getting all items userId={} ", ownerId);
        return itemClient.getAllItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchedItems(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Getting all searched items text={} ", text);
        return itemClient.getSearchedItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(OWNER_HEADER) int ownerId,
                                              @PathVariable int itemId,
                                              @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Creating comment={} userId={} itemId={} ", commentRequestDto, ownerId, itemId);
        return itemClient.postComment(ownerId, itemId, commentRequestDto);
    }
}
