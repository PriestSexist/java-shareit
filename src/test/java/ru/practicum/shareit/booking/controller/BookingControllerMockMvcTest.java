package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerMockMvcTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    private final LocalDateTime start = LocalDateTime.of(2024, 10, 20, 22, 21);
    private final LocalDateTime end = LocalDateTime.of(2024, 10, 22, 22, 21);

    @Test
    void postBooking() throws Exception {

        BookingDto bookingDtoForPost = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        when(bookingService.postBooking(Mockito.anyInt(), Mockito.any(BookingDto.class)))
                .thenReturn(bookingDtoForPost);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoForPost))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForPost.getId())))
                .andExpect(jsonPath("$.itemId", is(bookingDtoForPost.getItemId())))
                .andExpect(jsonPath("$.status", is(bookingDtoForPost.getStatus().name())));

    }

    @Test
    void postBookingThrowsBookingTimeException() throws Exception {

        BookingDto bookingDtoForPost = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.FALSE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        when(bookingService.postBooking(Mockito.anyInt(), Mockito.any(BookingDto.class)))
                .thenThrow(new BookingTimeException("Error with booking time"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoForPost))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingTimeException))
                .andExpect(result -> assertEquals("Error with booking time", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void postBookingThrowsItemNotAvailableForBookingException() throws Exception {

        BookingDto bookingDtoForPost = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.FALSE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        when(bookingService.postBooking(Mockito.anyInt(), Mockito.any(BookingDto.class)))
                .thenThrow(new ItemNotAvailableForBookingException("Item not available for booking due to available = false"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoForPost))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotAvailableForBookingException))
                .andExpect(result -> assertEquals("Item not available for booking due to available = false", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void postBookingThrowsBookingIdException() throws Exception {

        BookingDto bookingDtoForPost = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.FALSE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.WAITING);

        when(bookingService.postBooking(Mockito.anyInt(), Mockito.any(BookingDto.class)))
                .thenThrow(new BookingIdException("Item not available for booking due to you can't book your own item"));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoForPost))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingIdException))
                .andExpect(result -> assertEquals("Item not available for booking due to you can't book your own item", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void patchBooking() throws Exception {

        BookingDto bookingDtoForPost = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);

        when(bookingService.patchBooking(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenReturn(bookingDtoForPost);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(Boolean.TRUE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForPost.getId())))
                .andExpect(jsonPath("$.itemId", is(bookingDtoForPost.getItemId())))
                .andExpect(jsonPath("$.status", is(bookingDtoForPost.getStatus().name())));

    }

    @Test
    void patchBookingThrowsBookingNotFoundException() throws Exception {

        when(bookingService.patchBooking(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenThrow(new BookingNotFoundException("Booking not found"));

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(Boolean.TRUE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingNotFoundException))
                .andExpect(result -> assertEquals("Booking not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void patchBookingThrowsBookingStatusException() throws Exception {

        when(bookingService.patchBooking(Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyInt()))
                .thenThrow(new BookingStatusException("You already approved booking"));

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(Boolean.TRUE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingStatusException))
                .andExpect(result -> assertEquals("You already approved booking", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void getBookingById() throws Exception {

        BookingDto bookingDtoForPost = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);

        when(bookingService.getBookingById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingDtoForPost);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoForPost.getId())))
                .andExpect(jsonPath("$.itemId", is(bookingDtoForPost.getItemId())))
                .andExpect(jsonPath("$.status", is(bookingDtoForPost.getStatus().name())));
    }

    @Test
    void getItemsThatIBooked() throws Exception {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        when(bookingService.getItemsThatIBooked(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoBeforeWork1, bookingDtoBeforeWork2));

        mvc.perform(get("/bookings", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoBeforeWork1.getId())))
                .andExpect(jsonPath("$[0].itemId", is(bookingDtoBeforeWork1.getItemId())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoBeforeWork1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(bookingDtoBeforeWork2.getId())))
                .andExpect(jsonPath("$[1].itemId", is(bookingDtoBeforeWork2.getItemId())))
                .andExpect(jsonPath("$[1].status", is(bookingDtoBeforeWork2.getStatus().name())));
    }

    @Test
    void getBookingsOfMyItems() throws Exception {

        BookingDto bookingDtoBeforeWork1 = new BookingDto(1, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor B", "vitekb650@gmail.com")), new User(2, "Kick", "kick@gmail.com"), BookingStatus.APPROVED);
        BookingDto bookingDtoBeforeWork2 = new BookingDto(2, 1, start, end, new Item(1, "Дрель", "Базированная дрель", Boolean.TRUE, new User(3, "Viktor", "vitekb@gmail.com")), new User(4, "VB", "VB@gmail.com"), BookingStatus.APPROVED);

        when(bookingService.getBookingsOfMyItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(bookingDtoBeforeWork1, bookingDtoBeforeWork2));

        mvc.perform(get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoBeforeWork1.getId())))
                .andExpect(jsonPath("$[0].itemId", is(bookingDtoBeforeWork1.getItemId())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoBeforeWork1.getStatus().name())))
                .andExpect(jsonPath("$[1].id", is(bookingDtoBeforeWork2.getId())))
                .andExpect(jsonPath("$[1].itemId", is(bookingDtoBeforeWork2.getItemId())))
                .andExpect(jsonPath("$[1].status", is(bookingDtoBeforeWork2.getStatus().name())));
    }

    @Test
    void getBookingsOfMyItemsThrowsBookingStateException() throws Exception {

        when(bookingService.getBookingsOfMyItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new BookingStateException("Unknown state: UNSUPPORTED_STATUS"));

        mvc.perform(get("/bookings/owner", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BookingStateException))
                .andExpect(result -> assertEquals("Unknown state: UNSUPPORTED_STATUS", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }
}