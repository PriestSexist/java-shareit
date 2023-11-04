package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemItemRequestConnection;
import ru.practicum.shareit.request.storage.ItemItemRequestConnectionRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ItemRequestRepository itemRequestRepository;
    private final ItemItemRequestConnectionRepository itemItemRequestConnectionRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemDto postItem(int ownerId, ItemDto itemDto) {

        User userFromDb = userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = ItemMapper.createItem(itemDto, userFromDb);
        Item itemFormDb = itemRepository.save(item);

        if (itemDto.getRequestId() == null) {
            return ItemMapper.createItemDtoWithoutComments(itemFormDb);
        }

        itemRequestRepository.findItemRequestById(itemDto.getRequestId()).orElseThrow(() -> new ItemRequestNotFoundException("item request not found"));

        ItemItemRequestConnection itemItemRequestConnection = new ItemItemRequestConnection(0, itemFormDb.getId(), itemDto.getRequestId());
        itemItemRequestConnectionRepository.save(itemItemRequestConnection);

        return ItemMapper.createItemDtoWithoutCommentsAndWithRequestId(itemFormDb, itemDto.getRequestId());

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ItemDto patchItem(int itemId, int ownerId, ItemDto itemDto) {

        User userFromDb = userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Item itemFromDb = itemRepository.findItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (itemFromDb.getOwner().getId() != ownerId) {
            throw new WrongIdException("You don't have access to patch this object");
        }

        Item item = ItemMapper.createItem(itemDto, userFromDb);
        item.setId(itemId);

        if (item.getName() != null) {
            itemFromDb.setName(item.getName());
        }

        if (item.getDescription() != null) {
            itemFromDb.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            itemFromDb.setAvailable(item.getAvailable());
        }

        return ItemMapper.createItemDtoWithoutComments(itemRepository.save(itemFromDb));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public ItemDtoWithBooking getItemById(int ownerId, int itemId) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        Item itemFromDb = itemRepository.findItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        List<CommentDto> commentDtoList = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::createCommentDto)
                .collect(Collectors.toList());

        if (ownerId == itemFromDb.getOwner().getId()) {

            List<Booking> list = bookingRepository.findLastByItem_OwnerId(itemId, ownerId, BookingStatus.REJECTED, localDateTimeNow);
            List<Booking> list1 = bookingRepository.findNextByItem_OwnerId(itemId, ownerId, BookingStatus.REJECTED, localDateTimeNow);

            Optional<Booking> lastBookingOptional = list.stream().findFirst();
            Optional<Booking> nextBookingOptional = list1.stream().findFirst();

            return ItemMapper.createItemDtoWithBooking(itemFromDb,
                    BookingMapper.createShortBooking(lastBookingOptional.orElse(null)),
                    BookingMapper.createShortBooking(nextBookingOptional.orElse(null)),
                    commentDtoList);
        }
        return ItemMapper.createItemDtoWithBooking(itemFromDb, null, null, commentDtoList);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteItemById(int itemId) {

        itemRepository.findItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        itemRepository.deleteById(itemId);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<ItemDtoWithBooking> getAllItems(int ownerId, int from, int size) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRepository.findAllByOwnerIdOrderById(ownerId, pageRequest)
                .map(item -> ItemMapper.createItemDtoWithBooking(item,
                        BookingMapper.createShortBooking(bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(item.getId(), BookingStatus.REJECTED, localDateTimeNow).stream().findFirst().orElse(null)),
                        BookingMapper.createShortBooking(bookingRepository.findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStart(item.getId(), BookingStatus.REJECTED, localDateTimeNow).stream().findFirst().orElse(null)),
                        commentRepository.findAllByItemId(item.getId()).stream()
                                .map(CommentMapper::createCommentDto)
                                .collect(Collectors.toList())))
                .getContent();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<ItemDto> getSearchedItems(String text, int from, int size) {

        if (text.isBlank()) {
            return new ArrayList<>();
        }

        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, Boolean.TRUE, pageRequest)
                .map(item -> ItemMapper.createItemDto(item, commentRepository.findAllByItemId(item.getId()).stream()
                        .map(CommentMapper::createCommentDto)
                        .collect(Collectors.toList())))
                .getContent();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CommentDto postComment(int ownerId, int itemId, CommentDto commentDto) {

        LocalDateTime localDateTimeNow = LocalDateTime.now();

        bookingRepository.findFirstByBookerIdAndEndBeforeAndStatus(ownerId, localDateTimeNow, BookingStatus.APPROVED).orElseThrow(() -> new CommentNotFoundException("Booking not found"));

        User userFromDb = userRepository.findUserById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Item itemFromDb = itemRepository.findItemById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        Comment comment = CommentMapper.createComment(commentDto, userFromDb, itemFromDb);
        comment.setCreated(localDateTimeNow);

        return CommentMapper.createCommentDto(commentRepository.save(comment));

    }
}
