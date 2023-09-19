package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto postUser(UserDto userDto);

    UserDto patchUser(int userId, UserDto userDto);

    UserDto getUserById(int userId);

    void deleteUserById(int userId);

    Collection<UserDto> getAllUsers();
}
