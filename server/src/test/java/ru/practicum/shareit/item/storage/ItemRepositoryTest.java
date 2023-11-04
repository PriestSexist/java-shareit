package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForRequest;
import ru.practicum.shareit.request.model.ItemItemRequestConnection;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemItemRequestConnectionRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

@Transactional
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    ItemItemRequestConnectionRepository itemItemRequestConnectionRepository;

    @Test
    void findAllItemsForRequestByRequestId() {

        User userBeforeWorkItemOwner = new User(1, "Viktor B", "vitekb650@gmail.com");
        User userBeforeWorkItemBooker = new User(2, "Kick", "Kick@gmail.com");
        User userPostedOwner = userRepository.save(userBeforeWorkItemOwner);
        User userPostedBooker = userRepository.save(userBeforeWorkItemBooker);

        ItemRequest itemRequestForPost = new ItemRequest(1, "Хончу пива", LocalDateTime.now(), userPostedBooker);
        ItemRequest itemRequestPosted = itemRequestRepository.save(itemRequestForPost);

        Item itemForPost = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, userPostedOwner);
        Item itemPosted = itemRepository.save(itemForPost);

        ItemItemRequestConnection itemItemRequestConnectionForPost = new ItemItemRequestConnection(1, itemPosted.getId(), itemRequestPosted.getId());
        itemItemRequestConnectionRepository.save(itemItemRequestConnectionForPost);

        ItemForRequest itemForRequestShouldBe = new ItemForRequest(itemPosted.getId(), "Дрель", "Базированная дрель", Boolean.TRUE, itemRequestPosted.getId());

        ItemForRequest itemForRequest = itemRepository.findAllItemsForRequestByRequestId(itemRequestPosted.getId()).stream().findFirst().get();

        Assertions.assertEquals(itemForRequestShouldBe, itemForRequest);
    }
}