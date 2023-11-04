package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDto postUser(UserDto userDto) {
        User user = UserMapper.createUser(userDto);
        return UserMapper.createUserDto(userRepository.save(user));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDto patchUser(int userId, UserDto userDto) {

        User userFromDb = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userDto.getEmail()!=null) {
            userFromDb.setEmail(userDto.getEmail());
        }

        if (userDto.getName()!=null) {
            userFromDb.setName(userDto.getName());
        }

        return UserMapper.createUserDto(userRepository.save(userFromDb));
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public UserDto getUserById(int userId) {
        User userFromDb = userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.createUserDto(userFromDb);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserById(int userId) {
        userRepository.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.deleteById(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::createUserDto)
                .collect(Collectors.toList());
    }
}
