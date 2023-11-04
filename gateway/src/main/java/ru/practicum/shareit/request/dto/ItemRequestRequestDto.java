package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ItemRequestRequestDto {
    @NotBlank
    private final String description;
}
