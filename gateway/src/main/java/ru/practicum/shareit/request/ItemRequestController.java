package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postItemRequest(@Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto,
                                                  @RequestHeader(OWNER_HEADER) int ownerId) {
        log.info("Creating item request {}, userId={}", itemRequestRequestDto, ownerId);
        return itemRequestClient.postItemRequest(ownerId, itemRequestRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(OWNER_HEADER) int ownerId) {
        log.info("Getting item request, userId={}", ownerId);
        return itemRequestClient.getItemRequests(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(OWNER_HEADER) int ownerId,
                                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Getting all item request, userId={}", ownerId);
        return itemRequestClient.getAllItemRequests(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(OWNER_HEADER) int ownerId,
                                                     @PathVariable int requestId) {
        log.debug("Вызван метод getItemRequestById");
        return itemRequestClient.getItemRequestById(ownerId, requestId);
    }
}
