package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;

    private ItemRequestService itemRequestService;

    private static final LocalDateTime DATE = LocalDateTime.of(2024, 10, 20, 14, 37);

    @BeforeEach
    public void generator() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestRepository, itemRepository);
    }

    @Test
    void postItemRequest() {

        ItemRequestDto itemRequestDtoBeforeWork = new ItemRequestDto(1, "Хончу пива", DATE, null);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(new ItemRequest(1, "Хончу пива", DATE, new User(1, "Viktor B", "vitekb650@gmail.com")));

        ItemRequestDto itemRequestDtoAfterWork = itemRequestService.postItemRequest(itemRequestDtoBeforeWork, 1);

        Assertions.assertEquals(itemRequestDtoBeforeWork, itemRequestDtoAfterWork);
    }

    @Test
    void getItemRequests() {

        ItemRequestDto itemRequestDtoBeforeWork1 = new ItemRequestDto(1, "Хончу пива", DATE, new ArrayList<>());
        ItemRequestDto itemRequestDtoBeforeWork2 = new ItemRequestDto(2, "Хончу рома", DATE, new ArrayList<>());

        List<ItemRequestDto> itemRequestDtoListBeforeWork = List.of(itemRequestDtoBeforeWork1, itemRequestDtoBeforeWork2);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRequestRepository.findAllByUserIdOrderByCreatedAsc(Mockito.anyInt()))
                .thenReturn(List.of(new ItemRequest(1, "Хончу пива", DATE, new User(1, "Viktor B", "vitekb650@gmail.com")), new ItemRequest(2, "Хончу рома", DATE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(itemRepository.findAllItemsForRequestByRequestId(Mockito.anyInt()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> itemRequestDtoListAfterWork = itemRequestService.getItemRequests(1);

        Assertions.assertEquals(itemRequestDtoListBeforeWork, itemRequestDtoListAfterWork);
    }

    @Test
    void getAllItemRequests() {

        ItemRequestDto itemRequestDtoBeforeWork1 = new ItemRequestDto(1, "Хончу пива", DATE, new ArrayList<>());
        ItemRequestDto itemRequestDtoBeforeWork2 = new ItemRequestDto(2, "Хончу рома", DATE, new ArrayList<>());

        List<ItemRequestDto> itemRequestDtoListBeforeWork = List.of(itemRequestDtoBeforeWork1, itemRequestDtoBeforeWork2);

        List<ItemRequest> itemRequests = List.of(new ItemRequest(1, "Хончу пива", DATE, new User(1, "Viktor B", "vitekb650@gmail.com")), new ItemRequest(2, "Хончу рома", DATE, new User(1, "Viktor B", "vitekb650@gmail.com")));

        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRequestRepository.findAllByUserIdNotOrderByCreatedAsc(Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(itemRequestPage);

        Mockito.when(itemRepository.findAllItemsForRequestByRequestId(Mockito.anyInt()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> itemRequestDtoListAfterWork = itemRequestService.getAllItemRequests(1, 0, 5);

        Assertions.assertEquals(itemRequestDtoListBeforeWork, itemRequestDtoListAfterWork);

    }

    @Test
    void getItemRequestByIdThrowItemRequestNotFoundException() {

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRequestRepository.findItemRequestById(Mockito.anyInt()))
                .thenThrow(new ItemRequestNotFoundException("Item request not found"));

        Assertions.assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequestById(1, 1));
    }

    @Test
    void getItemRequestById() {

        ItemRequestDto itemRequestDtoBeforeWork = new ItemRequestDto(1, "Хончу пива", DATE, new ArrayList<>());

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRequestRepository.findItemRequestById(Mockito.anyInt()))
                .thenReturn(Optional.of(new ItemRequest(1, "Хончу пива", DATE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(itemRepository.findAllItemsForRequestByRequestId(Mockito.anyInt()))
                .thenReturn(new ArrayList<>());

        ItemRequestDto itemRequestDtoAfterWork = itemRequestService.getItemRequestById(1, 1);

        Assertions.assertEquals(itemRequestDtoBeforeWork, itemRequestDtoAfterWork);

    }
}