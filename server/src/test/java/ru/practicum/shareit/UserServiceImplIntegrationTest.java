package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource("classpath:application-test.properties")
class UserServiceImplIntegrationTest {

    final EntityManager em;
    final UserService userService;

    @Test
    void postUser() {
        UserDto userDtoForPost = new UserDto(0, "Viktor B", "vitekb650@gmail.com");

        UserDto userDtoPosted = userService.postUser(userDtoForPost);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDtoPosted.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDtoPosted.getId()));
        assertThat(user.getName(), equalTo(userDtoForPost.getName()));
        assertThat(user.getEmail(), equalTo(userDtoForPost.getEmail()));
    }

    @Test
    void patchUser() {

        UserDto userDtoForPost1 = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoForPost2 = new UserDto(1, "Kick", "vitekb650@gmail.com");

        UserDto userDtoPosted = userService.postUser(userDtoForPost1);
        UserDto userDtoPatched = userService.patchUser(userDtoPosted.getId(), userDtoForPost2);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDtoPatched.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDtoPatched.getId()));
        assertThat(user.getName(), equalTo(userDtoPatched.getName()));
        assertThat(user.getEmail(), equalTo(userDtoPatched.getEmail()));
    }

    @Test
    void getUserById() {

        UserDto userDtoBeforeWork = new UserDto(1, "Kick", "vitekb650@gmail.com");

        UserDto userDtoPosted = userService.postUser(userDtoBeforeWork);

        UserDto userDtoGot = userService.getUserById(userDtoPosted.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDtoPosted.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDtoGot.getId()));
        assertThat(user.getName(), equalTo(userDtoGot.getName()));
        assertThat(user.getEmail(), equalTo(userDtoGot.getEmail()));
    }

    @Test
    void deleteUserById() {

        UserDto userDtoBeforeWork = new UserDto(1, "Kick", "vitekb650@gmail.com");

        UserDto userDtoPosted = userService.postUser(userDtoBeforeWork);
        userService.deleteUserById(userDtoPosted.getId());

        Assertions.assertThrows(NoResultException.class, () -> {
            TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
            query.setParameter("id", userDtoBeforeWork.getId()).getSingleResult();
        });

    }

    @Test
    void getAllUsers() {

        UserDto userDtoBeforeWork1 = new UserDto(1, "Viktor B", "vitekb650@gmail.com");
        UserDto userDtoBeforeWork2 = new UserDto(1, "Kick", "kick@gmail.com");

        userService.postUser(userDtoBeforeWork1);
        userService.postUser(userDtoBeforeWork2);

        List<UserDto> userDtoListAfterWork = userService.getAllUsers();

        System.out.println(userService.getAllUsers());

        TypedQuery<User> query = em.createQuery("Select u from User u ", User.class);
        List<User> userList = query.getResultList();

        List<UserDto> userDtoList = userList.stream()
                .map(UserMapper::createUserDto)
                .collect(Collectors.toList());
        Assertions.assertEquals(userDtoList, userDtoListAfterWork);

    }
}