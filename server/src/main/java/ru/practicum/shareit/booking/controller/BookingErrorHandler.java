package ru.practicum.shareit.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.BookingErrorResponse;

@RestControllerAdvice
public class BookingErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BookingErrorResponse handleItemNotAvailableForBookingException(final ItemNotAvailableForBookingException exception) {
        return new BookingErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BookingErrorResponse handleBookingNotFoundException(final BookingNotFoundException exception) {
        return new BookingErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BookingErrorResponse handleBookingIdException(final BookingIdException exception) {
        return new BookingErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BookingErrorResponse handleBookingStatusException(final BookingStatusException exception) {
        return new BookingErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BookingErrorResponse handleBookingStateException(final BookingStateException exception) {
        return new BookingErrorResponse(exception.getMessage());
    }
}

