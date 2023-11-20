package ru.practicum.shareit.request.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequestErrorResponse;

@RestControllerAdvice
public class ItemRequestErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ItemRequestErrorResponse handleItemRequestNotFoundException(final ItemRequestNotFoundException exception) {
        return new ItemRequestErrorResponse(exception.getMessage());
    }
}
