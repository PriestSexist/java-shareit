package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class CommentDto {
    private final int id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}
