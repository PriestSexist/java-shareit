package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CommentRequestDto {
    @NotBlank
    private final String text;
    private final String authorName;
}
