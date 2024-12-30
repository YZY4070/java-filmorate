package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.FriendForHimselfException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    protected int nextId = 1;
    

    private boolean emailChecker(User user) {
        boolean emailExis = users.values().stream()
                .anyMatch(otherUser -> !otherUser.getId().equals(user.getId()) && otherUser.getEmail().equals(user.getEmail()));

        if (emailExis) throw new ValidationException("Пользователь с таким имейлом уже сущестует");
        return false;
    }

    private long getNextId() {
        return nextId++;
    }

//    private void friendsChecker(Long id, Long friendId) {
//        Stream.of(id, friendId)
//                .filter(Objects::isNull)
//                .findAny()
//                .ifPresent(nullId -> {
//                    log.error("id пользователя или его друга не указан");
//                    throw new ValidationException("Id пользователя или друга должен быть указан");
//                });
//
//        if (Stream.of(id, friendId).anyMatch(userId -> !users.containsKey(userId))) {
//            log.error("Пользователя с данным id или его друга не существует");
//            throw new NotFoundException("Пользователь с данным id или его друга не найден");
//        }
//
//        if (id.equals(friendId)) {
//            log.error("id пользователя и друга идентичны");
//            throw new FriendForHimselfException("Пользователь не может быть сам себе другом");
//        }
//    }

    private void friendsChecker(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.error("id пользователя или его друга не указан");
            throw new ValidationException("Id пользователя или друга должен быть указан");
        }
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            log.error("Пользователя с данным id или его друга не существует");
            throw new NotFoundException("Пользователь с данным id или его друга не найден");
        }
        if (id.equals(friendId)) {
            log.error("id пользователя и друга идентичны");
            throw new FriendForHimselfException("Пользователь не может быть сам себе другом");
        }
    }

    @Override
    public User create(@Valid User user) {
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

    public User update(@Valid User user) {
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

        users.put(user.getId(), exisUser);
        log.info("Пользователь с id {} успешно обновлен", user.getId());

        return exisUser;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Получение пользователя по id");
        if (users.get(id) == null) throw new NotFoundException("Такого пользователя не существует");
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return users.values();
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        log.info("Добавление друга");
        friendsChecker(id, friendId);
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.error("Уже друзья!");
            throw new AlreadyFriendsException("Пользователи уже друзья");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        log.info("удаление друга");
        friendsChecker(id, friendId);
        User user = getUserById(id);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        log.info("Получение друзей пользователя");
        if (id == null) {
            throw new ValidationException("Id не указан");
        }
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с данным не найден");
        }
        User user = getUserById(id);
        return user.getFriends().stream().map(this::getUserById).toList();
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long anotherId) {
        log.info("Получение общих друзей");
        friendsChecker(id, anotherId);

        Set<Long> user = getUserById(id).getFriends();
        Set<Long> friend = getUserById(anotherId).getFriends();

        if (friend.isEmpty() || user.isEmpty()) {
            throw new NotFoundException("Пользователи без друзей ;(");
        }
        return friend.stream().filter(user::contains).map(this::getUserById).toList();
    }
}
