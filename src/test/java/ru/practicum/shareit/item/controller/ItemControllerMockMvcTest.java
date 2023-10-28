package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.exception.CommentNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.WrongIdException;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(controllers = ItemController.class)
class ItemControllerMockMvcTest {

    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    static final LocalDateTime DATE = LocalDateTime.of(2022, 10, 20, 14, 37);

    @Test
    void postItem() throws Exception {

        ItemDto itemDtoForPost = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null);

        when(itemService.postItem(Mockito.anyInt(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDtoForPost);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoForPost))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForPost.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoForPost.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForPost.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForPost.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoForPost.getOwnerId())));
    }

    @Test
    void patchItem() throws Exception {

        ItemDto itemDtoForPatch = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null);

        when(itemService.patchItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDtoForPatch);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoForPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForPatch.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoForPatch.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForPatch.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForPatch.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoForPatch.getOwnerId())));
    }

    @Test
    void patchItemThrowsItemNotFoundException() throws Exception {

        ItemDto itemDtoForPatch = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null);

        when(itemService.patchItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(ItemDto.class)))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoForPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotFoundException))
                .andExpect(result -> assertEquals("Item not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void patchItemThrowsWrongIdException() throws Exception {

        ItemDto itemDtoForPatch = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null);

        when(itemService.patchItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(ItemDto.class)))
                .thenThrow(new WrongIdException("You don't have access to patch this object"));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoForPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WrongIdException))
                .andExpect(result -> assertEquals("You don't have access to patch this object", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void getItemById() throws Exception {

        ItemDtoWithBooking itemDtoWithBookingForGet = new ItemDtoWithBooking(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null, null);

        when(itemService.getItemById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDtoWithBookingForGet);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBookingForGet.getId())))
                .andExpect(jsonPath("$.name", is(itemDtoWithBookingForGet.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBookingForGet.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBookingForGet.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoWithBookingForGet.getOwnerId())));
    }

    @Test
    void deleteItemById() throws Exception {

        mvc.perform(delete("/items/{id}", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        Mockito.verify(itemService, Mockito.times(1))
                .deleteItemById(1);
    }

    @Test
    void getAllItems() throws Exception {

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null);
        ItemDto itemDtoForPost2 = new ItemDto(2, "Дрелька", "Базированная дрелька", Boolean.TRUE, 1, null, null);

        Mockito.when(itemService.getAllItems(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(new ItemDtoWithBooking(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null, null), new ItemDtoWithBooking(2, "Дрелька", "Базированная дрелька", Boolean.TRUE, 1, null, null, null)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoForPost1.getId())))
                .andExpect(jsonPath("$[0].name", is(itemDtoForPost1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoForPost1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoForPost1.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDtoForPost1.getOwnerId())))
                .andExpect(jsonPath("$[1].id", is(itemDtoForPost2.getId())))
                .andExpect(jsonPath("$[1].name", is(itemDtoForPost2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoForPost2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoForPost2.getAvailable())))
                .andExpect(jsonPath("$[1].ownerId", is(itemDtoForPost2.getOwnerId())));
    }

    @Test
    void getSearchedItems() throws Exception {

        ItemDto itemDtoForPost1 = new ItemDto(1, "Дрель", "Базированная дрель", Boolean.TRUE, 1, null, null);
        ItemDto itemDtoForPost2 = new ItemDto(2, "Дрелька", "Базированная дрелька", Boolean.TRUE, 1, null, null);

        Mockito.when(itemService.getSearchedItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemDtoForPost1, itemDtoForPost2));

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoForPost1.getId())))
                .andExpect(jsonPath("$[0].name", is(itemDtoForPost1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoForPost1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoForPost1.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDtoForPost1.getOwnerId())))
                .andExpect(jsonPath("$[1].id", is(itemDtoForPost2.getId())))
                .andExpect(jsonPath("$[1].name", is(itemDtoForPost2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoForPost2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDtoForPost2.getAvailable())))
                .andExpect(jsonPath("$[1].ownerId", is(itemDtoForPost2.getOwnerId())));
    }

    @Test
    void postComment() throws Exception {

        CommentDto commentDtoBeforeWork = new CommentDto(1, "норм", "Kick", DATE);

        when(itemService.postComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(CommentDto.class)))
                .thenReturn(commentDtoBeforeWork);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDtoBeforeWork))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoBeforeWork.getId())))
                .andExpect(jsonPath("$.text", is(commentDtoBeforeWork.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoBeforeWork.getAuthorName())));
    }

    @Test
    void postCommentThrowsCommentNotFoundException() throws Exception {

        CommentDto commentDtoBeforeWork = new CommentDto(1, "норм", "Kick", DATE);

        when(itemService.postComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(CommentDto.class)))
                .thenThrow(new CommentNotFoundException("Booking not found"));

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDtoBeforeWork))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentNotFoundException))
                .andExpect(result -> assertEquals("Booking not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }
}