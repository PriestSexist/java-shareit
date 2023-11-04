package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestControllerServer {

    private final ItemRequestService itemRequestService;
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto postItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                          @RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод postItemRequest");
        return itemRequestService.postItemRequest(itemRequestDto, ownerId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод getItemRequests");
        return itemRequestService.getItemRequests(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(OWNER_HEADER) int ownerId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.debug("Вызван метод getAllItemRequests");
        return itemRequestService.getAllItemRequests(ownerId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(OWNER_HEADER) int ownerId,
                                             @PathVariable int requestId) {
        log.debug("Вызван метод getItemRequestById");
        return itemRequestService.getItemRequestById(ownerId, requestId);
    }
}
