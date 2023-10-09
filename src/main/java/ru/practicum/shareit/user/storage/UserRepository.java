package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserById(Integer id);


    @Modifying
    @Query("UPDATE User u SET u.name = ?1 WHERE u.id = ?2")
    void updateUserNameById(String name, Integer id);

    @Modifying
    @Query("UPDATE User u SET u.email = ?1 WHERE u.id = ?2")
    void updateUserEmailById(String email, Integer id);

    Optional<User> findUserByEmail(String email);
}
