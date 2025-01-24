package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.FriendForHimselfException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcFriendsRepository;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JdbcUserRepository userRepository;
    private final JdbcFriendsRepository friendsRepository;

    public User create(User user) {
        if (user.getLogin().chars().anyMatch(Character::isWhitespace)) {
            log.error("Ошибка добавления");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при добавлении юзера");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (emailChecker(user)) {
            log.error("Ошибка добавления");
            throw new ValidationException("Пользователь с такой почтой уже существует");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Так как имя пустое, мы добавляем в поле логин");
            user.setName(user.getLogin());
        }

        return userRepository.create(user);
    }

    public User update(User user) {
        log.info("Начало внесение изменений в существующего пользователя");
        if (user.getId() == null) {
            log.error("Ошибка при добавлении");
            throw new ValidationException("Нету id юзера");
        }
        User exisUser = userRepository.getUserById(user.getId());
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
        return userRepository.update(user);
    }

    public User getUserById(Long id) {
        if (userRepository.getUserById(id) == null) throw new NotFoundException("Такого пользователя не существует");
        return userRepository.getUserById(id);
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Добавление друга");
        friendsChecker(id, friendId);
        User user = userRepository.getUserById(id);
        User friend = userRepository.getUserById(friendId);

        if (user.getFriends() != null && user.getFriends().equals(friend)) {
            log.error("Уже друзья!");
            throw new AlreadyFriendsException("Пользователи уже друзья");
        }

        friendsRepository.addFriend(friendId, id);
    }


    public void deleteFriend(Long id, Long friendId) {
        log.info("удаление друга");
        friendsChecker(id, friendId);
        User user = userRepository.getUserById(id);
        User friend = userRepository.getUserById(friendId);

        friendsRepository.removeFriend(friendId, id);
    }

    public Collection<User> getCommonFriends(Long id, Long anotherId) {
        log.info("Получение общих друзей");
        friendsChecker(id, anotherId);

        Set<Long> userFriends = userRepository.getUserById(id).getFriends();
        Set<Long> friendFriends = userRepository.getUserById(anotherId).getFriends();

        if (friendFriends.isEmpty() || userFriends.isEmpty()) {
            throw new NotFoundException("Пользователи без друзей ;(");
        }

        return friendsRepository.getCommonFriends(id, anotherId);
    }

    public Collection<User> getFriends(Long id) {
        log.info("Получение друзей пользователя");
       userRepository.getUserById(id);
       return friendsRepository.getFriends(id);
    }

    private void friendsChecker(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.error("id пользователя или его друга не указан");
            throw new ValidationException("Id пользователя или друга должен быть указан");
        }

        userRepository.getUserById(id);
        userRepository.getUserById(friendId);

        if (id.equals(friendId)) {
            log.error("id пользователя и друга идентичны");
            throw new FriendForHimselfException("Пользователь не может быть сам себе другом");
        }
    }

    private boolean emailChecker(User user) {
        boolean emailExis = userRepository.getAllUsers().stream()
                .anyMatch(otherUser -> !otherUser.getId().equals(user.getId()) && otherUser.getEmail().equals(user.getEmail()));
        if (emailExis) throw new ValidationException("Пользователь с таким имейлом уже сущестует");
        return false;
    }
}
