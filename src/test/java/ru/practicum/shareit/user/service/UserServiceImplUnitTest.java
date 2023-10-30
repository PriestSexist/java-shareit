package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    UserService userService;

    @BeforeEach
    void generator() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void postUser() {
        UserDto userDtoBeforeSave = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1, "Viktor B", "vitekb650@gmail.com"));

        UserDto userDtoAfterSave = userService.postUser(userDtoBeforeSave);
        Assertions.assertEquals(userDtoBeforeSave, userDtoAfterSave);
    }

    @Test
    void patchUserThrowsUserNotFoundException() {

        UserDto userDtoPatchUser = new UserDto(1, "Kick", "vitekb650@gmail.com");

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenThrow(new UserNotFoundException("User not found"));

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.patchUser(11, userDtoPatchUser));
    }

    @Test
    void patchUser() {

        UserDto userDtoPatchUserName = new UserDto(1, "Kick", "vitekb650@gmail.com");

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User(1, "Kick", "vitekb650@gmail.com"));

        UserDto userDtoAfterSave = userService.patchUser(1, userDtoPatchUserName);
        Assertions.assertEquals(userDtoPatchUserName, userDtoAfterSave);
    }


    @Test
    void getUserById() {
        UserDto userDtoBeforeSave = new UserDto(1, "Viktor B", "vitekb650@gmail.com");

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        UserDto userDtoAfterSave = userService.getUserById(1);
        Assertions.assertEquals(userDtoAfterSave, userDtoBeforeSave);
    }

    @Test
    void deleteUserById() {

        Mockito.when(userRepository.findUserById(Mockito.anyInt()))
                .thenReturn(Optional.of(new User(1, "Viktor B", "vitekb650@gmail.com")));

        userService.deleteUserById(1);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1);
    }

    @Test
    void getAllUsers() {
        UserDto userDto1 = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDto2 = new UserDto(2, "Kick", "Kcik@gmail.com");

        ArrayList<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto1);
        userDtos.add(userDto2);

        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(new User(1, "Viktor B", "vitekb650@gmail.com"), new User(2, "Kick", "Kcik@gmail.com")));

        List<UserDto> userDtos1 = userService.getAllUsers();

        Assertions.assertEquals(userDtos1, userDtos);
    }
}
