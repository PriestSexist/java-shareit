package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.exception.CommentNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongIdException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemDto postItem(int ownerId, ItemDto itemDto) {

        Optional<User> userFromDb = userRepository.findUserById(ownerId);


        if (userFromDb.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        Item item = ItemMapper.createItem(itemDto, userFromDb.get());


        return ItemMapper.createItemDtoWithoutComments(itemRepository.save(item));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemDto patchItem(int itemId, int ownerId, ItemDto itemDto) {

        Optional<Item> itemFromDb = itemRepository.findItemById(itemId);
        Optional<User> userFromDb = userRepository.findUserById(ownerId);

        if (userFromDb.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        if (itemFromDb.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        if (itemFromDb.get().getOwner().getId() != ownerId) {
            throw new WrongIdException("You don't have access to patch this object");
        }


        Item item = ItemMapper.createItem(itemDto, userFromDb.get());
        item.setId(itemId);

        if (item.getName() != null) {
            itemFromDb.get().setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemFromDb.get().setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemFromDb.get().setAvailable(item.getAvailable());
        }

        // Если сделать таким образом, то в базе данных изменения не меняются до 3 вызова метода. Я не знаю почему.
        // Хотя, что забавно, подобный способ работает с UserService и я не знаю почему и какой вариант правильнее.
        // Мне нравится вариант, который сейчас в ItemService выше

        /*if (item.getAvailable()!=null) {
            itemRepository.updateItemAvailableById(item.getAvailable(), itemId);
            System.out.println(itemRepository.findAll());
        }*/

        return ItemMapper.createItemDtoWithoutComments(itemRepository.save(itemFromDb.get()));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemDtoWithBooking getItemById(int ownerId, int itemId) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        Optional<Item> itemFromDb = itemRepository.findItemById(itemId);

        if (itemFromDb.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        List<CommentDto> commentDtoList = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::createCommentDto)
                .collect(Collectors.toList());

        if (ownerId == itemFromDb.get().getOwner().getId()) {

            List<Booking> list = bookingRepository.findLastByItem_OwnerId(itemId, ownerId, BookingStatus.REJECTED, localDateTimeNow);
            List<Booking> list1 = bookingRepository.findNextByItem_OwnerId(itemId, ownerId, BookingStatus.REJECTED, localDateTimeNow);
            Optional<Booking> lastBookingOptional = list.stream().findFirst();
            Optional<Booking> nextBookingOptional = list1.stream().findFirst();

            return ItemMapperWithBooking.createItemDtoWithBooking(itemFromDb.get(),
                    BookingMapper.createShortBooking(lastBookingOptional.orElse(null)),
                    BookingMapper.createShortBooking(nextBookingOptional.orElse(null)),
                    commentDtoList);
        }
        return ItemMapperWithBooking.createItemDtoWithBooking(itemFromDb.get(), null, null, commentDtoList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteItemById(int itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    // МБ n+1 проблема? Если да, то как тогда лучше?
    public Collection<ItemDtoWithBooking> getAllItems(int ownerId) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        return itemRepository.findAllByOwnerId(ownerId).stream().map(item -> ItemMapperWithBooking.createItemDtoWithBooking(item,
                        BookingMapper.createShortBooking(bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(item.getId(), BookingStatus.REJECTED, localDateTimeNow).stream().findFirst().orElse(null)),
                        BookingMapper.createShortBooking(bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStart(item.getId(), BookingStatus.REJECTED, localDateTimeNow).stream().findFirst().orElse(null)),
                        commentRepository.findAllByItemId(item.getId()).stream()
                                .map(CommentMapper::createCommentDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Collection<ItemDto> getSearchedItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, Boolean.TRUE).stream()
                .map(item -> ItemMapper.createItemDto(item, commentRepository.findAllByItemId(item.getId()).stream()
                        .map(CommentMapper::createCommentDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CommentDto postComment(int ownerId, int itemId, CommentDto commentDto) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        Optional<Booking> bookingFromDb = bookingRepository.findFirstByBookerIdAndEndBeforeAndStatus(ownerId, localDateTimeNow, BookingStatus.APPROVED);
        Optional<User> userFromDb = userRepository.findUserById(ownerId);
        Optional<Item> itemFromDb = itemRepository.findItemById(itemId);

        if (bookingFromDb.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }

        if (userFromDb.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        if (itemFromDb.isEmpty()) {
            throw new ItemNotFoundException("Item not found");
        }

        Comment comment = CommentMapper.createComment(commentDto, userFromDb.get(), itemFromDb.get());

        return CommentMapper.createCommentDto(commentRepository.save(comment));

    }
}
