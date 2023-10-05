package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findBookingById(Integer id);

    //Можно так или лучше через Query?
    Optional<Booking> findByBookerIdAndIdOrItem_OwnerIdAndId(Integer bookerId, Integer id, Integer ownerId, Integer sameId);

    List<Booking> findAllByBookerId(Integer bookerId);

    List<Booking> findAllByStartIsBeforeAndEndIsAfterAndBookerId(LocalDateTime localDateTime, LocalDateTime sameLocalDateTime, Integer bookerId);

    List<Booking> findAllByEndIsBeforeAndBookerId(LocalDateTime localDateTime, Integer bookerId);

    List<Booking> findAllByStartIsAfterAndBookerId(LocalDateTime localDateTime, Integer bookerId);

    List<Booking> findAllByStatusAndBookerId(BookingStatus status, Integer bookerId);

    List<Booking> findAllByItem_OwnerId(Integer ownerId);

    List<Booking> findAllByStartIsBeforeAndEndIsAfterAndItem_OwnerId(LocalDateTime localDateTime, LocalDateTime sameLocalDateTime, Integer ownerId);

    List<Booking> findAllByEndIsBeforeAndItem_OwnerId(LocalDateTime localDateTime, Integer ownerId);

    List<Booking> findAllByStartIsAfterAndItem_OwnerId(LocalDateTime localDateTime, Integer ownerId);

    List<Booking> findAllByStatusAndItem_OwnerId(BookingStatus status, Integer ownerId);

    Optional<Booking> findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStart(Integer itemId, BookingStatus status, LocalDateTime currentTime);

    Optional<Booking> findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(Integer itemId, BookingStatus status, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastByItem_OwnerId(Integer itemId, Integer ownerId, BookingStatus status, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextByItem_OwnerId(Integer itemId, Integer ownerId, BookingStatus status, LocalDateTime currentTime);

    Optional<Booking> findFirstByBookerIdAndEndBeforeAndStatus(Integer bookerId, LocalDateTime currentTime, BookingStatus status);
}