package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemRequestDto postItemRequest(ItemRequestDto itemRequestDto, int ownerId) {

        LocalDateTime currentTime = LocalDateTime.now();

        User user = userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        ItemRequest itemRequest = ItemRequestMapper.createItemRequest(itemRequestDto);
        itemRequest.setCreated(currentTime);
        itemRequest.setUser(user);

        return ItemRequestMapper.createItemRequestDtoWithoutItems(itemRequestRepository.save(itemRequest));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<ItemRequestDto> getItemRequests(int ownerId) {

        userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedAsc(ownerId);
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.createItemRequestDto(itemRequest,
                        itemRepository.findAllItemsForRequestByRequestId(itemRequest.getId()).stream()
                                .map(ItemMapper::createItemForRequestDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<ItemRequestDto> getAllItemRequests(int ownerId, int from, int size) {

        userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdNotOrderByCreatedAsc(ownerId, pageRequest);

        return itemRequests.map(itemRequest -> ItemRequestMapper.createItemRequestDto(itemRequest,
                itemRepository.findAllItemsForRequestByRequestId(itemRequest.getId()).stream()
                        .map(ItemMapper::createItemForRequestDto)
                        .collect(Collectors.toList()))).getContent();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemRequestDto getItemRequestById(int ownerId, int requestId) {

        userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId).orElseThrow(() -> new ItemRequestNotFoundException("Item request not found"));

        return ItemRequestMapper.createItemRequestDto(itemRequest, itemRepository.findAllItemsForRequestByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::createItemForRequestDto)
                .collect(Collectors.toList()));
    }


}
