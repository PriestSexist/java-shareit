package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.CommentNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongIdException;
import ru.practicum.shareit.item.model.ItemErrorResponse;

@RestControllerAdvice
public class ItemErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        return new ItemErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemErrorResponse handleWrongIdException(final WrongIdException exception) {
        return new ItemErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ItemErrorResponse handleCommentNotFoundException(final CommentNotFoundException exception) {
        return new ItemErrorResponse(exception.getMessage());
    }
}
