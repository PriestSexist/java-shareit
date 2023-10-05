package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Вызван метод postUser");
        return userServiceImpl.postUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@RequestBody UserDto userDto, @PathVariable int id) {
        log.debug("Вызван метод patchUser");
        return userServiceImpl.patchUser(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable int id) {
        log.debug("Вызван метод getUserById");
        return userServiceImpl.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        log.debug("Вызван метод deleteUserById");
        userServiceImpl.deleteUserById(id);
    }

    @GetMapping()
    public Collection<UserDto> getAllUsers() {
        log.debug("Вызван метод getAllUsers");
        return userServiceImpl.getAllUsers();
    }
}
