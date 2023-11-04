package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto postItemRequest(ItemRequestDto itemRequestDto, int ownerId);

    List<ItemRequestDto> getItemRequests(int ownerId);

    List<ItemRequestDto> getAllItemRequests(int ownerId, int from, int size);

    ItemRequestDto getItemRequestById(int ownerId, int requestId);
}
