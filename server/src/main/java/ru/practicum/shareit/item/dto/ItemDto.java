package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDto {
    private final int id;
    //@NotBlank(message = "name can't be blank")
    private final String name;
    //@NotBlank(message = "description can't be blank")
    private final String description;
    //@NotNull(message = "available can't be null")
    private final Boolean available;
    private final int ownerId;
    private final List<CommentDto> comments;
    private final Integer requestId;
}