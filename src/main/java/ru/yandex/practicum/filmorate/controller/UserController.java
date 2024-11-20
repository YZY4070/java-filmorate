package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    protected int nextId = 1;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Список пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавление пользователя");

        if (user.getLogin().chars().anyMatch(Character::isWhitespace)) {
            log.error("Ошибка добавления");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при добавлении юзера");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Так как имя пустое, мы добавляем в поле логин");
            user.setName(user.getLogin());

        }

        if (emailChecker(user)) {
            log.error("Ошибка добавления");
            throw new ValidationException("Пользователь с такой почтой уже существует");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Начало внесение изменений в существующего пользователя");
        if (user.getId() == null) {
            log.error("Ошибка при добавлении");
            throw new ValidationException("Нету id юзера");
        }

        User exisUser = users.get(user.getId());
        if (exisUser == null) {
            log.error("Ошибка обновления пользователя");
            throw new NotFoundException("Пользователь не найден");
        }

        if (emailChecker(user)) {
            log.error("Ошибка при обновлении данных юзера");
            throw new ValidationException("Этот имейл уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Добавляем логин если имя пустое");
            user.setName(user.getLogin());
        }

        exisUser = exisUser.toBuilder()
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();

        log.info("Пользователь с id {} успешно обновлен", user.getId());

        return exisUser;
    }

    private long getNextId() {
        return nextId++;
    }

    private boolean emailChecker (User user){
        return users.values().stream()
                .anyMatch(otherUser -> !otherUser.getId().equals(user.getId()) && otherUser.getEmail().equals(user.getEmail()));
    }

}
