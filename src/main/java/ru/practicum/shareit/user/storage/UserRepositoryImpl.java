package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.SameEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Integer, User> users = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    @Override
    public User postUser(User user) {

        if (users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new SameEmailException("This email was registered");
        }

        user.setId(counter.incrementAndGet());

        users.put(user.getId(), user);
        log.debug("пользователь занесён в бд");
        return user;
    }

    @Override
    public User patchUser(User user) {

        User userForReplace = users.get(user.getId());

        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User not fount");
        }

        if (users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail())) && !userForReplace.getEmail().equals(user.getEmail())) {
            throw new SameEmailException("This email was registered");
        }

        if (user.getEmail()!=null) {
            userForReplace.setEmail(user.getEmail());
        }

        if (user.getName()!=null) {
            userForReplace.setName(user.getName());
        }

        users.replace(userForReplace.getId(), userForReplace);
        log.debug("пользователь изменён в бд");
        return userForReplace;
    }

    @Override
    public User getUserById(int userId) {
        try {
            return users.get(userId);
        } catch (NullPointerException exception) {
            throw new UserNotFoundException("User not fount");
        }
    }

    @Override
    public void deleteUser(int userId) {
        if (users.remove(userId)==null) {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }
}
