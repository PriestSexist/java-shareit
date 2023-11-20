package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findBookingById(Integer id);

    Optional<Booking> findByBookerIdAndIdOrItem_OwnerIdAndId(Integer bookerId, Integer id, Integer ownerId, Integer sameId);

    Page<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId, Pageable pageable);

    Page<Booking> findAllByStartBeforeAndEndAfterAndBookerId(LocalDateTime localDateTime, LocalDateTime sameLocalDateTime, Integer bookerId, Pageable pageable);

    Page<Booking> findAllByEndIsBeforeAndBookerIdOrderByStartDesc(LocalDateTime localDateTime, Integer bookerId, Pageable pageable);

    Page<Booking> findAllByStartIsAfterAndBookerIdOrderByStartDesc(LocalDateTime localDateTime, Integer bookerId, Pageable pageable);

    Page<Booking> findAllByStatusAndBookerIdOrderByStartDesc(BookingStatus status, Integer bookerId, Pageable pageable);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(Integer ownerId, Pageable pageable);

    Page<Booking> findAllByStartBeforeAndEndAfterAndItem_OwnerId(LocalDateTime localDateTime, LocalDateTime sameLocalDateTime, Integer ownerId, Pageable pageable);

    Page<Booking> findAllByEndIsBeforeAndItem_OwnerIdOrderByStartDesc(LocalDateTime localDateTime, Integer ownerId, Pageable pageable);

    Page<Booking> findAllByStartIsAfterAndItem_OwnerIdOrderByStartDesc(LocalDateTime localDateTime, Integer ownerId, Pageable pageable);

    Page<Booking> findAllByStatusAndItem_OwnerIdOrderByStartDesc(BookingStatus status, Integer ownerId, Pageable pageable);

    Optional<Booking> findFirstBookingByItem_IdAndStatusNotAndStartAfterOrderByStart(Integer itemId, BookingStatus status, LocalDateTime currentTime);

    Optional<Booking> findFirstBookingByItem_IdAndStatusNotAndStartBeforeOrderByStartDesc(Integer itemId, BookingStatus status, LocalDateTime currentTime);

    @Query(value = "select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.owner.id = ?2 " +
            "and b.status <> ?3 " +
            "and b.start <= now() " +
            "order by b.start desc ")
    List<Booking> findLastByItem_OwnerId(Integer itemId, Integer ownerId, BookingStatus status);

    @Query(value = "select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.item.owner.id = ?2 " +
            "and b.status <> ?3 " +
            "and b.start > now()  " +
            "order by b.start ")
    List<Booking> findNextByItem_OwnerId(Integer itemId, Integer ownerId, BookingStatus status);

    Optional<Booking> findFirstByBookerIdAndEndBefore(Integer bookerId, LocalDateTime currentTime);
}