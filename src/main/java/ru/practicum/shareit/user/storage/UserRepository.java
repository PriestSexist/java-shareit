package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    User postUser(User user);

    User patchUser(User user);

    User getUserById(int userId);

    void deleteUser(int userId);

    Collection<User> getAllUsers();
}
