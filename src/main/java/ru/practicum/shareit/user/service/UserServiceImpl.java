package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepositoryImpl;

    @Override
    public UserDto postUser(UserDto userDto) {
        User user = UserMapper.createUser(userDto);
        return UserMapper.createUserDto(userRepositoryImpl.postUser(user));
    }

    @Override
    public UserDto patchUser(int userId, UserDto userDto) {
        User user = UserMapper.createUser(userDto);
        user.setId(userId);
        return UserMapper.createUserDto(userRepositoryImpl.patchUser(user));
    }

    @Override
    public UserDto getUserById(int userId) {
        return UserMapper.createUserDto(userRepositoryImpl.getUserById(userId));
    }

    @Override
    public void deleteUserById(int userId) {
        userRepositoryImpl.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepositoryImpl.getAllUsers().stream()
                .map(UserMapper::createUserDto)
                .collect(Collectors.toList());
    }
}
