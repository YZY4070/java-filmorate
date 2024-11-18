package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        logger.info("Список фильмов");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user){
        logger.info("Добавление фильма");

        if (user.getLogin().contains(" ")){
            logger.error("Ошибка добавления");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            logger.error("Ошибка при добавлении юзера");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()){
            logger.info("Так как имя пустое, мы добавляем в поле логин");
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        logger.info("Добавлен пользователь");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user){
        logger.info("Начало внесение изменений в существующего пользователя");
        if (user.getId() == null){
            logger.error("Ошибка при добавлении");
            throw new ValidationException("Нету id юзера");
        }

        User exisUser = users.get(user.getId());
        if (exisUser == null){
            logger.error("Ошибка обновления пользователя");
            throw new NotFoundException("Пользователь не найден");
        }

        boolean emailExists = users.values().stream()
                .anyMatch(otherUser -> !otherUser.getId().equals(user.getId()) && otherUser.getEmail().equals(user.getEmail()));

        if (emailExists) {
            logger.error("Ошибка при обновлении данных юзера");
            throw new ValidationException("Этот имейл уже используется");
        }

        exisUser = exisUser.toBuilder()
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();

        logger.info("Пользователь с id {} успешно обновлен", user.getId());

        return exisUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
