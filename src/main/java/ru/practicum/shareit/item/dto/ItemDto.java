package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private final int id;
    @NotBlank(message = "name can't be blank")
    private final String name;
    @NotBlank(message = "description can't be blank")
    private final String description;
    @NotNull(message = "available can't be null")
    private final Boolean available;
    private final int ownerId;
    private final HashMap<Integer, String> review = new HashMap<>();
}
