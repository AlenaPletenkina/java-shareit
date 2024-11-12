package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.services.UserService;

//import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {
    private final UserService userService;
    private final UserRepository userRepositoryJpa;
    UserDto userDto1;
    User user1;
    UserDto userDto2;
    User user2;
    User userNull;
    UserDto userDtoNull;
    User userAllFieldsNull;
    UserDto userDtoAllFieldsNull;

    @BeforeEach
    void setUp() {
        userDto1 = UserDto.builder()
                .name("name userDto1")
                .email("userDto1@mans.gf")
                .build();
        user1 = User.builder().id(userDto1.getId()).name(userDto1.getName()).email(userDto1.getEmail()).build();

        userDto2 = UserDto.builder()
                .name("name userDto2")
                .email("userDto2@mans.gf")
                .build();
        user2 = User.builder().id(userDto2.getId()).name(userDto2.getName()).email(userDto2.getEmail()).build();

        userAllFieldsNull = new User();

        userNull = null;
        userDtoNull = null;

    }

    @Test
    void getUserById_WhenAllIsOkTest() {
        UserDto savedUser = userService.createUser(userDto1);

        UserDto user = userService.getUser(savedUser.getId());

        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto1.getName());
        assertEquals(user.getEmail(), userDto1.getEmail());
    }

    @Test
    void getUserById_whenUserNotFoundInDb_returnTest() {
        UserDto savedUser = userService.createUser(userDto1);

        assertThrows(ObjectNotFoundException.class,
                () -> userService.getUser(9000L));
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        List<UserDto> userDtos = List.of(userDto1, userDto2);

        userService.createUser(userDto1);
        userService.createUser(userDto2);


        Collection<UserDto> result = userService.getAllUsers();

        assertEquals(userDtos.size(), result.size());
        for (UserDto user : userDtos) {
            assertThat(result, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(user.getName())),
                    hasProperty("email", equalTo(user.getEmail()))
            )));
        }
    }

    @SneakyThrows
    @Test
    void addUserTest() {
        userService.createUser(userDto1);

        Collection<UserDto> users = userService.getAllUsers();
        boolean result = false;
        Long id = users.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);

        UserDto userDtoFromDb = userService.getUser(id);

        assertEquals(1, users.size());
        assertEquals(userDto1.getName(), userDtoFromDb.getName());
        assertEquals(userDto1.getEmail(), userDtoFromDb.getEmail());
    }

    @SneakyThrows
    @Test
    void updateInStorage_whenAllIsOkAndNameIsNull_returnUpdatedUserTest() {
        UserDto createdUser = userService.createUser(userDto1);

        Collection<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUser(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userService.updateUser(createdUser.getId(), userDto2);

        UserDto userDtoFromDbAfterUpdate = userService.getUser(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto2.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto2.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOkAndEmailIsNull_returnUpdatedUserTest() {
        UserDto createdUser = userService.createUser(userDto1);

        Collection<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUser(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userService.updateUser(createdUser.getId(), userDto2);

        UserDto userDtoFromDbAfterUpdate = userService.getUser(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto2.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto2.getEmail());
    }

    @Test
    void updateInStorage_whenAllIsOk_returnUpdatedUserTest() {
        UserDto createdUser = userService.createUser(userDto1);

        Collection<UserDto> beforeUpdateUsers = userService.getAllUsers();
        Long id = beforeUpdateUsers.stream()
                .filter(u -> u.getEmail().equals(userDto1.getEmail()))
                .findFirst()
                .map(UserDto::getId).orElse(null);
        assertNotNull(id);
        assertEquals(id, createdUser.getId());

        UserDto userDtoFromDbBeforeUpdate = userService.getUser(id);

        assertEquals(userDtoFromDbBeforeUpdate.getName(), userDto1.getName());
        assertEquals(userDtoFromDbBeforeUpdate.getEmail(), userDto1.getEmail());

        userService.updateUser(createdUser.getId(), userDto2);

        UserDto userDtoFromDbAfterUpdate = userService.getUser(id);

        assertEquals(userDtoFromDbBeforeUpdate.getId(), userDtoFromDbAfterUpdate.getId());
        assertEquals(userDtoFromDbAfterUpdate.getName(), userDto2.getName());
        assertEquals(userDtoFromDbAfterUpdate.getEmail(), userDto2.getEmail());
    }

    @Test
    void updateInStorage_whenUserNotFound_returnNotFoundRecordInBDTest() {
        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () ->
                userService.updateUser(55L, userDto1));
        assertEquals("Пользователь с id " +
                55 + " не найден", ex.getMessage());
    }

    @Test
    void removeFromStorageTest() {
        UserDto savedUser = userService.createUser(userDto1);
        Collection<UserDto> beforeDelete = userService.getAllUsers();

        assertEquals(1, beforeDelete.size());

        userService.deleteUser(savedUser.getId());
        Collection<UserDto> afterDelete = userService.getAllUsers();

        assertEquals(0, afterDelete.size());
    }
}
