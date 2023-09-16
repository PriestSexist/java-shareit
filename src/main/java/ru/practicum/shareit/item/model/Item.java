package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int ownerId;
    private final HashMap<Integer, String> review = new HashMap<>();
}
