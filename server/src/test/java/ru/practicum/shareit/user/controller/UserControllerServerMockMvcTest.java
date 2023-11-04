package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserControllerServer.class)
class UserControllerServerMockMvcTest {

    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;


    @Test
    void postUser() throws Exception {

        UserDto userDtoForPost = new UserDto(1, "Viktor B", "vitekb650@gmail.com");

        when(userService.postUser(Mockito.any(UserDto.class)))
                .thenReturn(userDtoForPost);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoForPost))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoForPost.getId())))
                .andExpect(jsonPath("$.name", is(userDtoForPost.getName())))
                .andExpect(jsonPath("$.email", is(userDtoForPost.getEmail())));
    }

    @Test
    void patchUser() throws Exception {

        UserDto userDtoForPatch = new UserDto(1, "Viktor B", "vitekb650@gmail.com");

        when(userService.patchUser(Mockito.anyInt(), Mockito.any(UserDto.class)))
                .thenReturn(userDtoForPatch);

        mvc.perform(patch("/users/{id}", 1)
                        .content(objectMapper.writeValueAsString(userDtoForPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoForPatch.getId())))
                .andExpect(jsonPath("$.name", is(userDtoForPatch.getName())))
                .andExpect(jsonPath("$.email", is(userDtoForPatch.getEmail())));
    }

    @Test
    void getUserById() throws Exception {

        UserDto userDtoForGet = new UserDto(1, "Viktor B", "vitekb650@gmail.com");

        when(userService.getUserById(Mockito.anyInt()))
                .thenReturn(userDtoForGet);

        mvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoForGet.getId())))
                .andExpect(jsonPath("$.name", is(userDtoForGet.getName())))
                .andExpect(jsonPath("$.email", is(userDtoForGet.getEmail())));
    }

    @Test
    void getUserByIdThrowsUserNotFoundException() throws Exception {

        when(userService.getUserById(Mockito.anyInt()))
                .thenThrow(new UserNotFoundException("User not found"));

        mvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals("User not found", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteUserById() throws Exception {

        mvc.perform(delete("/users/{id}", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        Mockito.verify(userService, Mockito.times(1))
                .deleteUserById(1);
    }

    @Test
    void getAllUsers() throws Exception {

        UserDto userDto1 = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDto2 = new UserDto(2, "Kick", "Kcik@gmail.com");

        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(new UserDto(1, "Viktor B", "vitekb650@gmail.com"), new UserDto(2, "Kick", "Kcik@gmail.com")));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto1.getId())))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId())))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
    }
}