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
    private final String name;
    private final String description;
    private final Boolean available;
    private final int ownerId;
    private final List<CommentDto> comments;
    private final Integer requestId;
}
