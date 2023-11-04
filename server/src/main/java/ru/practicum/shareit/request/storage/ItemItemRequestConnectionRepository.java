package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemItemRequestConnection;

@Repository
public interface ItemItemRequestConnectionRepository extends JpaRepository<ItemItemRequestConnection, Integer> {
}
