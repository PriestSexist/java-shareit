package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingServiceImpl bookingService;
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
    public Collection<BookingDto> getItemsThatIBooked(@RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод getItemsThatIBooked");
        return bookingService.getItemsThatIBooked(state, ownerId);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsOfMyItems(@RequestParam(defaultValue = "ALL") String state,
                                                       @RequestHeader(OWNER_HEADER) int ownerId) {
        log.debug("Вызван метод getBookingsOfMyItems");
        return bookingService.getBookingsOfMyItems(state, ownerId);
    }
}
