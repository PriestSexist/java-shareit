package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemRequestDto {
    @NotBlank(message = "name can't be blank")
    private final String name;
    @NotBlank(message = "description can't be blank")
    private final String description;
    @NotNull(message = "available can't be null")
    private final Boolean available;
    private final Integer requestId;
}
