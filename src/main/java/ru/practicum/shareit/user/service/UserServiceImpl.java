package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.SameEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.Optional;
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

        Optional<User> userFromDb = userRepository.findUserByEmail(userDto.getEmail());

        if (userFromDb.isPresent() && userFromDb.get().getId() != userId) {
            throw new SameEmailException("This email already exists");
        }

        User user = UserMapper.createUser(userDto);
        user.setId(userId);

        if (user.getName() == null && user.getEmail() != null) {
            userRepository.updateUserEmailById(user.getEmail(), userId);
            Optional<User> optionalUser = userRepository.findUserById(userId);
            return UserMapper.createUserDto(optionalUser.get());
        }

        if (user.getEmail() == null && user.getName() != null) {
            userRepository.updateUserNameById(user.getName(), userId);
            return UserMapper.createUserDto(userRepository.findUserById(userId).get());
        }

        return UserMapper.createUserDto(userRepository.save(user));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UserDto getUserById(int userId) {
        Optional<User> userFromDb = userRepository.findUserById(userId);
        if (userFromDb.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return UserMapper.createUserDto(userFromDb.get());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::createUserDto)
                .collect(Collectors.toList());
    }
}
