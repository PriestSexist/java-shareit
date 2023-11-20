package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findItemById(Integer id);

    Page<Item> findAllByOwnerIdOrderById(Integer ownerId, Pageable pageable);

    Page<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String text, String sameText, Boolean available, Pageable pageable);

    @Query("select new ru.practicum.shareit.item.model.ItemForRequest(it.id, it.name, it.description, it.available, ir.id) " +
            "from ItemItemRequestConnection as iirc " +
            "left join Item as it on it.id = iirc.itemId " +
            "left join ItemRequest as ir on ir.id = iirc.requestId " +
            "where ir.id = ?1 ")
    List<ItemForRequest> findAllItemsForRequestByRequestId(int requestId);

}
