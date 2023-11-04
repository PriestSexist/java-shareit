package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.exception.CommentNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongIdException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemItemRequestConnection;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemItemRequestConnectionRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemItemRequestConnectionRepository itemItemRequestConnectionRepository;

    ItemService itemService;
    static final LocalDateTime DATE = LocalDateTime.of(2023, 10, 20, 14, 37);

    @BeforeEach
    void generator() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository, itemItemRequestConnectionRepository);
    }

    @Test
    void postItem() {
        ItemDto itemDtoBeforeWork = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 3, null, null);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(3, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")));

        ItemDto itemDtoAfterWork = itemService.postItem(3, itemDtoBeforeWork);

        Assertions.assertEquals(itemDtoBeforeWork, itemDtoAfterWork);
    }

    @Test
    void postItemForRequest() {
        ItemDto itemDtoBeforeWork = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, 1);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRequestRepository.findItemRequestById(Mockito.anyInt()))
                .thenReturn(Optional.of(new ItemRequest(1, "Хочу дрель", DATE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        ItemDto itemDtoAfterWork = itemService.postItem(1, itemDtoBeforeWork);

        Mockito.verify(itemItemRequestConnectionRepository, Mockito.times(1))
                .save(Mockito.any(ItemItemRequestConnection.class));

        Assertions.assertEquals(itemDtoBeforeWork, itemDtoAfterWork);
    }

    @Test
    void patchItem() {
        ItemDto itemDtoBeforeWork = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, 1, null, null);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")));

        ItemDto itemDtoAfterWork = itemService.patchItem(1, 1, itemDtoBeforeWork);

        Assertions.assertEquals(itemDtoBeforeWork, itemDtoAfterWork);
    }

    @Test
    void patchItemThrowsWrongIdException() {
        ItemDto itemDtoBeforeWork = new ItemDto(1, "Дрелька", "Базированная дрелька", Boolean.TRUE, 1, null, null);

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Assertions.assertThrows(WrongIdException.class, () -> itemService.patchItem(1, 2, itemDtoBeforeWork));
    }

    @Test
    void getItemById() {

        List<CommentDto> commentList = new ArrayList<>();
        commentList.add(new CommentDto(1, "Норм штука", "Kick", DATE));
        ItemDtoWithBooking itemDtoBeforeWork = new ItemDtoWithBooking(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null, commentList);

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt()))
                .thenReturn(List.of(new Comment(1, "Норм штука", new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), DATE)));

        ItemDtoWithBooking itemDtoAfterWork = itemService.getItemById(1, 1);

        Assertions.assertEquals(itemDtoBeforeWork, itemDtoAfterWork);
    }

    @Test
    void getItemByIdNotOwnerException() {

        List<CommentDto> commentList = new ArrayList<>();
        commentList.add(new CommentDto(1, "Норм штука", "Kick", DATE));
        ItemDtoWithBooking itemDtoBeforeWork = new ItemDtoWithBooking(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null, commentList);

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt()))
                .thenReturn(List.of(new Comment(1, "Норм штука", new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), DATE)));

        ItemDtoWithBooking itemDtoAfterWork = itemService.getItemById(2, 1);

        Assertions.assertEquals(itemDtoBeforeWork, itemDtoAfterWork);
    }

    @Test
    void getItemByIdItemNotFoundException() {

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenThrow(new ItemNotFoundException("item not found"));

        Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1, 1));
    }

    @Test
    void deleteItemById() {

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        itemService.deleteItemById(1);

        Mockito.verify(itemRepository, Mockito.times(1))
                .deleteById(1);
    }

    @Test
    void getAllItems() {
        ItemDtoWithBooking itemDtoBeforeWork1 = new ItemDtoWithBooking(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null, new ArrayList<>());
        ItemDtoWithBooking itemDtoBeforeWork2 = new ItemDtoWithBooking(2, "Дрель2", "Базированная дрель2", Boolean.TRUE, 1, null, null, new ArrayList<>());

        Collection<ItemDtoWithBooking> itemDtoWithBookingCollectionBeforeWork = new ArrayList<>();

        itemDtoWithBookingCollectionBeforeWork.add(itemDtoBeforeWork1);
        itemDtoWithBookingCollectionBeforeWork.add(itemDtoBeforeWork2);

        Item item1 = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"));
        Item item2 = new Item(2, "Дрель2", "Базированная дрель2", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Page<Item> itemsPage = new PageImpl<>(items);

        Mockito.when(itemRepository.findAllByOwnerIdOrderById(Mockito.anyInt(), Mockito.any(PageRequest.class)))
                .thenReturn(itemsPage);

        Mockito.when(bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(Mockito.anyInt(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Mockito.when(bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStart(Mockito.anyInt(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt()))
                .thenReturn(new ArrayList<>());

        Collection<ItemDtoWithBooking> itemDtoWithBookingCollectionAfterWork = itemService.getAllItems(1, 0, 5);

        Assertions.assertEquals(itemDtoWithBookingCollectionAfterWork, itemDtoWithBookingCollectionBeforeWork);

    }

    @Test
    void getSearchedItems() {

        ItemDto itemDtoBeforeWork1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, new ArrayList<>(), null);
        ItemDto itemDtoBeforeWork2 = new ItemDto(2, "Дрель2", "Базированная дрель2", Boolean.TRUE, 1, new ArrayList<>(), null);

        Collection<ItemDto> itemDtoCollectionBeforeWork = new ArrayList<>();

        itemDtoCollectionBeforeWork.add(itemDtoBeforeWork1);
        itemDtoCollectionBeforeWork.add(itemDtoBeforeWork2);

        Item item1 = new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"));
        Item item2 = new Item(2, "Дрель2", "Базированная дрель2", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Page<Item> itemsPage = new PageImpl<>(items);

        Mockito.when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(PageRequest.class)))
                .thenReturn(itemsPage);

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt()))
                .thenReturn(new ArrayList<>());

        Collection<ItemDto> itemDtoCollectionAfterWork = itemService.getSearchedItems("Дрель", 0, 5);

        Assertions.assertEquals(itemDtoCollectionAfterWork, itemDtoCollectionBeforeWork);
    }

    @Test
    void getSearchedItemsWithBlankText() {

        Collection<ItemDto> itemDtoCollectionBeforeWork = new ArrayList<>();

        Collection<ItemDto> itemDtoCollectionAfterWork = itemService.getSearchedItems("", 0, 5);

        Assertions.assertEquals(itemDtoCollectionAfterWork, itemDtoCollectionBeforeWork);
    }

    @Test
    void postCommentCommentNotFoundException() {
        Mockito.when(bookingRepository.findFirstByBookerIdAndEndBefore(Mockito.anyInt(), Mockito.any(LocalDateTime.class)))
                .thenThrow(new CommentNotFoundException("Booking not found"));

        Assertions.assertThrows(CommentNotFoundException.class, () -> itemService.postComment(1, 1, new CommentDto(1, "норм", "Biktor V", DATE)));
    }

    @Test
    void postCommentComment() {

        CommentDto commentDtoBeforeWork = new CommentDto(1, "норм", "Viktor B", DATE);

        Mockito.when(bookingRepository.findFirstByBookerIdAndEndBefore(Mockito.anyInt(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking(1, DATE, DATE, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")), new User(1, "Viktor B", "vitekb650@gmail.com"), BookingStatus.WAITING)));

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(itemRepository.findItemById(Mockito.anyInt()))
                .thenReturn(Optional.of(new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com"))));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(new Comment(1, "норм", new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(1, "Viktor B", "vitekb650@gmail.com")), new User(1, "Viktor B", "vitekb650@gmail.com"), DATE));

        CommentDto commentDtoAfterWork = itemService.postComment(1, 1, commentDtoBeforeWork);

        Assertions.assertEquals(commentDtoAfterWork, commentDtoBeforeWork);
    }
}