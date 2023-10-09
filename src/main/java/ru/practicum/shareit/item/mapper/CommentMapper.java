package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public final class CommentMapper {
    public static Comment createComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .created(commentDto.getCreated())
                .build();
    }

    public static CommentDto createCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
