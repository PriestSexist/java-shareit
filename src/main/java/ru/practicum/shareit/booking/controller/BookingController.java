package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;
    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto postBooking(@RequestHeader(OWNER_HEADER) int ownerId,
                                  @Valid @RequestBody BookingDto bookingDto) {
        log.debug("Вызван метод postBooking");
        return bookingService.postBooking(ownerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@PathVariable int bookingId,
                                   @RequestParam Boolean approved,
                                   @RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод patchBooking");
        return bookingService.patchBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId,
                                     @RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод getBookingById");
        return bookingService.getBookingById(bookingId, ownerId);
    }

    @GetMapping
    public List<BookingDto> getItemsThatIBooked(@RequestParam(defaultValue = "ALL") String state,
                                                @RequestHeader(OWNER_HEADER) int ownerId,
                                                @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        log.debug("Вызван метод getItemsThatIBooked");
        return bookingService.getItemsThatIBooked(state, ownerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOfMyItems(@RequestHeader(OWNER_HEADER) int ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        log.debug("Вызван метод getBookingsOfMyItems");
        return bookingService.getBookingsOfMyItems(state, ownerId, from, size);
    }
}
