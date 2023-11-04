package ru.practicum.shareit.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.UserErrorResponse;

@RestControllerAdvice
public class UserErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UserErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        return new UserErrorResponse(exception.getMessage());
    }
}
