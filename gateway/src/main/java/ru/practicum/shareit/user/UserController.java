package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("Creating user {} ", userRequestDto);
        return userClient.postUser(userRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@RequestBody UserRequestDto userRequestDto,
                                            @PathVariable int id) {
        log.info("Patch user {}, userId={}", userRequestDto, id);
        return userClient.patchUser(id, userRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        log.info("Get user userId={}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        log.info("delete user userId={}", id);
        userClient.deleteUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("get all users");
        return userClient.getAllUsers();
    }
}
