package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerMockMvcTest {

    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    private static final LocalDateTime DATE = LocalDateTime.of(2024, 10, 20, 14, 37);


    @Test
    void postItemRequest() throws Exception {

        ItemRequestDto itemRequestDtoBeforeWork = new ItemRequestDto(1, "Хончу пива", DATE, null);

        when(itemRequestService.postItemRequest(Mockito.any(ItemRequestDto.class), Mockito.anyInt()))
                .thenReturn(itemRequestDtoBeforeWork);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDtoBeforeWork))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoBeforeWork.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoBeforeWork.getDescription())));
    }

    @Test
    void getItemRequests() throws Exception {

        ItemRequestDto itemRequestDtoBeforeWork1 = new ItemRequestDto(1, "Хончу пива", DATE, null);
        ItemRequestDto itemRequestDtoBeforeWork2 = new ItemRequestDto(2, "Хончу рома", DATE, new ArrayList<>());

        when(itemRequestService.getItemRequests(Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDtoBeforeWork1, itemRequestDtoBeforeWork2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoBeforeWork1.getId())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoBeforeWork1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDtoBeforeWork2.getId())))
                .andExpect(jsonPath("$[1].description", is(itemRequestDtoBeforeWork2.getDescription())));


    }

    @Test
    void getAllItemRequests() throws Exception {

        ItemRequestDto itemRequestDtoBeforeWork1 = new ItemRequestDto(1, "Хончу пива", DATE, null);
        ItemRequestDto itemRequestDtoBeforeWork2 = new ItemRequestDto(2, "Хончу рома", DATE, new ArrayList<>());

        when(itemRequestService.getAllItemRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDtoBeforeWork1, itemRequestDtoBeforeWork2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoBeforeWork1.getId())))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoBeforeWork1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDtoBeforeWork2.getId())))
                .andExpect(jsonPath("$[1].description", is(itemRequestDtoBeforeWork2.getDescription())));

    }

    @Test
    void getItemRequestById() throws Exception {

        ItemRequestDto itemRequestDtoBeforeWork = new ItemRequestDto(1, "Хончу пива", DATE, null);

        when(itemRequestService.getItemRequestById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemRequestDtoBeforeWork);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoBeforeWork.getId())))
                .andExpect(jsonPath("$.description", is(itemRequestDtoBeforeWork.getDescription())));
    }

    @Test
    void getItemRequestByIdItemThrowsRequestNotFoundException() throws Exception {

        when(itemRequestService.getItemRequestById(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ItemRequestNotFoundException("Item request not found"));

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemRequestNotFoundException))
                .andExpect(result -> assertEquals("Item request not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }
}