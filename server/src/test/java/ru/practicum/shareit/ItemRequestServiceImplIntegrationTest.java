package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource("classpath:application-test.properties")
class ItemRequestServiceImplIntegrationTest {

    final EntityManager em;
    final UserService userService;
    final ItemRequestService itemRequestService;
    static final LocalDateTime DATE = LocalDateTime.of(2023, 10, 20, 14, 37);

    @Test
    void postItemRequest() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemRequestDto itemRequestDtoForPost = new ItemRequestDto(1, "Хончу пива", DATE, null);
        ItemRequestDto itemRequestDtoPosted = itemRequestService.postItemRequest(itemRequestDtoForPost, userDtoPosted.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDtoPosted.getId()).getSingleResult();

        assertThat(itemRequest.getId(), equalTo(itemRequestDtoPosted.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDtoPosted.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDtoPosted.getCreated()));
    }

    @Test
    void getItemRequests() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemRequestDto itemRequestDtoForPost1 = new ItemRequestDto(1, "Хончу пива", DATE, null);
        ItemRequestDto itemRequestDtoForPost2 = new ItemRequestDto(1, "Хончу рома", DATE, new ArrayList<>());
        itemRequestService.postItemRequest(itemRequestDtoForPost1, userDtoPosted.getId());
        itemRequestService.postItemRequest(itemRequestDtoForPost2, userDtoPosted.getId());

        List<ItemRequestDto> itemRequestDto = itemRequestService.getItemRequests(userDtoPosted.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir ", ItemRequest.class);
        List<ItemRequestDto> itemRequestDtoShouldBe = query.getResultList().stream()
                .map(itemRequest -> ItemRequestMapper.createItemRequestDto(itemRequest, new ArrayList<>()))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());

        Assertions.assertEquals(itemRequestDto, itemRequestDtoShouldBe);
    }

    @Test
    void getAllItemRequests() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemRequestDto itemRequestDtoForPost1 = new ItemRequestDto(1, "Хончу пива", DATE, null);
        ItemRequestDto itemRequestDtoForPost2 = new ItemRequestDto(1, "Хончу рома", DATE, new ArrayList<>());
        itemRequestService.postItemRequest(itemRequestDtoForPost1, userDtoPostedOwner.getId());
        itemRequestService.postItemRequest(itemRequestDtoForPost2, userDtoPostedOwner.getId());

        List<ItemRequestDto> itemRequestDto = itemRequestService.getAllItemRequests(userDtoPostedBooker.getId(), 0, 5);

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir ", ItemRequest.class);
        List<ItemRequestDto> itemRequestDtoShouldBe = query.getResultList().stream()
                .map(itemRequest -> ItemRequestMapper.createItemRequestDto(itemRequest, new ArrayList<>()))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());

        Assertions.assertEquals(itemRequestDto, itemRequestDtoShouldBe);
    }

    @Test
    void getItemRequestById() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemRequestDto itemRequestDtoForPost1 = new ItemRequestDto(1, "Хончу пива", DATE, null);
        ItemRequestDto itemRequestDtoForPost2 = new ItemRequestDto(1, "Хончу рома", DATE, new ArrayList<>());
        ItemRequestDto itemRequestDtoPosted1 = itemRequestService.postItemRequest(itemRequestDtoForPost1, userDtoPosted.getId());
        itemRequestService.postItemRequest(itemRequestDtoForPost2, userDtoPosted.getId());

        ItemRequestDto itemRequestDtoGot = itemRequestService.getItemRequestById(userDtoPosted.getId(), itemRequestDtoPosted1.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDtoPosted1.getId()).getSingleResult();

        assertThat(itemRequest.getId(), equalTo(itemRequestDtoGot.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDtoGot.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDtoGot.getCreated()));
    }
}