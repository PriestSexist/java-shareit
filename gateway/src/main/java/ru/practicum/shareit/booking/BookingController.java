package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    private static final String OWNER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postBooking(@RequestHeader(OWNER_HEADER) int ownerId,
                                              @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Creating booking {}, userId={}", bookingRequestDto, ownerId);
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd()) || bookingRequestDto.getStart().equals(bookingRequestDto.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error with booking time");
        }
        return bookingClient.postBooking(ownerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@PathVariable int bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader(OWNER_HEADER) int ownerId) {
        log.info("Patch booking {}, userId={}", bookingId, ownerId);
        return bookingClient.patchBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable int bookingId,
                                                 @RequestHeader(OWNER_HEADER) int ownerId) {
        log.info("Get booking {}, userId={}", bookingId, ownerId);
        return bookingClient.getBookingById(ownerId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsThatIBooked(@RequestHeader(OWNER_HEADER) int ownerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("getItemsThatIBooked booking with state {}, userId={}, from={}, size={}", state, ownerId, from, size);
        return bookingClient.getItemsThatIBooked(ownerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfMyItems(@RequestHeader(OWNER_HEADER) int ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") @Min(0) int from,
                                                       @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("getBookingsOfMyItems booking with state {}, userId={}, from={}, size={}", state, ownerId, from, size);
        return bookingClient.getBookingsOfMyItems(ownerId, state, from, size);
    }

}
