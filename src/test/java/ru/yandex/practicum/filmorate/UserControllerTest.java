package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserControllerTest {
    UserController userController = new UserController();

    User user0 = User.builder()
            .id(Long.valueOf(23))
            .email("some@mail.ru")
            .login("somelogin")
            .name("some name")
            .birthday(LocalDate.of(2000, 12, 28))
            .build();

    User userPostman = User.builder()
            .login("dolore")
            .name("Nick Name")
            .email("mail@mail.ru")
            .birthday(LocalDate.of(1946, 8, 20))
            .build();


    @Test
    public void testFindAllMethodWithFilledUsersMap() throws Exception {
        userController.create(user0);
        assertEquals(userController.findAll().size(), 1);
    }

    @Test
    public void testCreateMethodWithEmptyLogin() throws Exception {
        user0.setLogin(" ");
        try {
            User testUserObj = userController.create(user0);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Логин не может содержать пробелы");
        }
    }

    @Test
    public void testCreateMethodWithBirthdayInFuture() throws Exception {
        user0.setBirthday(LocalDate.of(2300, 12, 28));
        try {
            User testUserObj = userController.create(user0);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Дата рождения не может быть в будущем");
        }
    }

    @Test
    public void testUpdateMethodWithNullId() throws Exception {
        user0.setId(null);
        try {
            userController.update(user0);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Нету id юзера");
        }
    }

    @Test
    public void testUpdateMethodWithSameEmail() throws Exception {
        userController.create(user0);
        User user1 = user0;
        user1.setId(Long.valueOf(10));

        try {
            userController.create(user1);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "Пользователь с такой почтой уже существует");
        }
    }

    @Test
    public void testUpdateMethodWithWrongId() throws Exception {
        userController.create(user0);
        User user1 = (user0);
        user1.setId(Long.valueOf(44));
        try {
            userController.update(user1);
        } catch (NotFoundException e) {
            assertEquals(e.getMessage(), "Пользователь не найден");
        }
    }

    @Test
    public void testUpdateMethodWithValidRequest() throws Exception {
        userController.create(user0);
        user0.setEmail("another@Mail");
        user0.setLogin("anotherLogin");
        user0.setName("anotherName");
        user0.setBirthday(LocalDate.of(2013, 12, 28));
        userController.update(user0);
        assertEquals(user0.getEmail(), "another@Mail");
        assertEquals(user0.getLogin(), "anotherLogin");
        assertEquals(user0.getName(), "anotherName");
        assertEquals(user0.getBirthday(), LocalDate.of(2013, 12, 28));
    }
}
