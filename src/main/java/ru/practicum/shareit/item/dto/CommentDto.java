package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class CommentDto {
    private final int id;
    @NotBlank
    private final String text;
    private final String authorName;
    private LocalDateTime created = LocalDateTime.now();
}
