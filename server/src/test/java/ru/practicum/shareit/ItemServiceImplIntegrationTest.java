package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"DATASOURCE_URL=jdbc:postgresql://localhost:5432/shareit", "POSTGRES_USER=root", "POSTGRES_PASSWORD=root"})
class ItemServiceImplIntegrationTest {

    final EntityManager em;
    final ItemService itemService;
    final UserService userService;
    final BookingService bookingService;

    static final LocalDateTime DATE = LocalDateTime.of(2022, 10, 20, 14, 37);

    @Test
    void postItem() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPosted.getId(), itemDtoForPost);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoPosted.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoPosted.getId()));
        assertThat(item.getName(), equalTo(itemDtoPosted.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoPosted.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoPosted.getAvailable()));

    }

    @Test
    void patchItem() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoForPatch = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPosted.getId(), itemDtoForPost);
        ItemDto itemDtoPatched = itemService.patchItem(itemDtoPosted.getId(), userDtoPosted.getId(), itemDtoForPatch);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoPosted.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDtoPatched.getId()));
        assertThat(item.getName(), equalTo(itemDtoPatched.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoPatched.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoPatched.getAvailable()));
    }

    @Test
    void getItemById() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPosted.getId(), itemDtoForPost);
        ItemDtoWithBooking itemDtoGot = itemService.getItemById(userDtoPosted.getId(), itemDtoPosted.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoPosted.getId()).getSingleResult();

        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.createItemDtoWithBooking(item, null, null, new ArrayList<>());

        assertThat(itemDtoWithBooking.getId(), equalTo(itemDtoGot.getId()));
        assertThat(itemDtoWithBooking.getName(), equalTo(itemDtoGot.getName()));
        assertThat(itemDtoWithBooking.getDescription(), equalTo(itemDtoGot.getDescription()));
        assertThat(itemDtoWithBooking.getAvailable(), equalTo(itemDtoGot.getAvailable()));
    }

    @Test
    void deleteItemById() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPosted.getId(), itemDtoForPost);
        itemService.deleteItemById(itemDtoPosted.getId());

        Assertions.assertThrows(NoResultException.class, () -> {
            TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
            query.setParameter("id", itemDtoPosted.getId()).getSingleResult();
        });

    }

    @Test
    void getAllItems() {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPosted.getId(), null, null);
        itemService.postItem(userDtoPosted.getId(), itemDtoForPost1);
        itemService.postItem(userDtoPosted.getId(), itemDtoForPost2);

        List<ItemDtoWithBooking> itemDtoWithBookingList = itemService.getAllItems(userDtoPosted.getId(), 0, 5);

        TypedQuery<Item> query = em.createQuery("Select i from Item i ", Item.class);
        List<Item> itemList = query.getResultList();

        List<ItemDtoWithBooking> itemDtoWithBookingListShouldBe = itemList.stream()
                .map(item -> ItemMapper.createItemDtoWithBooking(item, null, null, new ArrayList<>()))
                .collect(Collectors.toList());

        Assertions.assertEquals(itemDtoWithBookingListShouldBe, itemDtoWithBookingList);
    }

    @Test
    void getSearchedItems() {

        String text = "Дрель";

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPosted.getId(), null, null);
        ItemDto itemDtoForPost2 = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, userDtoPosted.getId(), null, null);
        itemService.postItem(userDtoPosted.getId(), itemDtoForPost1);
        itemService.postItem(userDtoPosted.getId(), itemDtoForPost2);

        List<ItemDto> itemDtoWithBookingList = itemService.getSearchedItems(text, 0, 5);

        TypedQuery<Item> query = em.createQuery("Select i from Item i ", Item.class);
        List<Item> itemList = query.getResultList();

        List<ItemDto> itemDtoWithBookingListShouldBe = itemList.stream()
                .map(item -> ItemMapper.createItemDto(item, new ArrayList<>()))
                .collect(Collectors.toList());

        Assertions.assertEquals(itemDtoWithBookingListShouldBe, itemDtoWithBookingList);
    }

    @Test
    void postComment() {

        UserDto userDtoBeforeWorkItemOwner = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWorkItemBooker = new UserDto(1, "Kick", "Kick@gmail.com");
        UserDto userDtoPostedOwner = userService.postUser(userDtoBeforeWorkItemOwner);
        UserDto userDtoPostedBooker = userService.postUser(userDtoBeforeWorkItemBooker);

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, userDtoPostedOwner.getId(), null, null);
        ItemDto itemDtoPosted = itemService.postItem(userDtoPostedOwner.getId(), itemDtoForPost);

        BookingDto bookingDtoForPost = new BookingDto(1, itemDtoPosted.getId(), DATE.minusYears(2), DATE.minusYears(2).plusDays(3), ItemMapper.createItem(itemDtoPosted, UserMapper.createUser(userDtoPostedOwner)), UserMapper.createUser(userDtoPostedBooker), BookingStatus.APPROVED);
        BookingDto bookingDtoPosted = bookingService.postBooking(userDtoPostedBooker.getId(), bookingDtoForPost);
        bookingService.patchBooking(bookingDtoPosted.getId(), Boolean.TRUE, userDtoPostedOwner.getId());

        CommentDto commentDtoBeforeWork = new CommentDto(1, "норм", "Kick", DATE);
        CommentDto commentDtoPosted = itemService.postComment(userDtoPostedBooker.getId(), itemDtoPosted.getId(), commentDtoBeforeWork);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDtoPosted.getId()).getSingleResult();

        assertThat(comment.getId(), equalTo(commentDtoPosted.getId()));
        assertThat(comment.getText(), equalTo(commentDtoPosted.getText()));
        assertThat(comment.getAuthor().getName(), equalTo(commentDtoPosted.getAuthorName()));
        assertThat(comment.getCreated(), equalTo(commentDtoPosted.getCreated()));
    }
}