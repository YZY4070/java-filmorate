package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    protected int nextId = 1;

    private long getNextId() {
        return nextId++;
    }

    @Override
    public User create(User user) {
        log.info("Добавление пользователя");

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Так как имя пустое, мы добавляем в поле логин");
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь");
        return user;
    }

    public User update(User user) {
        User exisUser = users.get(user.getId());

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

        users.put(user.getId(), exisUser);
        log.info("Пользователь с id {} успешно обновлен", user.getId());
        return exisUser;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Получение пользователя по id");
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return users.values();
    }
}
