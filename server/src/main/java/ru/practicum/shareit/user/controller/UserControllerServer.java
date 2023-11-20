package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerServer {

    private final UserService userService;

    @PostMapping
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.debug("Вызван метод postUser");
        return userService.postUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@RequestBody UserDto userDto,
                             @PathVariable int id) {
        log.debug("Вызван метод patchUser");
        return userService.patchUser(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.debug("Вызван метод getUserById");
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        log.debug("Вызван метод deleteUserById");
        userService.deleteUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Вызван метод getAllUsers");
        return userService.getAllUsers();
    }
}
