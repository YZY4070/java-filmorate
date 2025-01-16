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
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь");
        return user;
    }

    public User update(User user) {
        User exisUser = users.get(user.getId());
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
