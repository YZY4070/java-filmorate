package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.FriendForHimselfException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(Long id, Long friendId) {
        log.info("Добавление друга");
        friendsChecker(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.error("Уже друзья!");
            throw new AlreadyFriendsException("Пользователи уже друзья");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }


    public void deleteFriend(Long id, Long friendId) {
        log.info("удаление друга");
        friendsChecker(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public Collection<User> getCommonFriends(Long id, Long anotherId) {
        log.info("Получение общих друзей");
        friendsChecker(id, anotherId);

        Set<Long> userFriends = userStorage.getUserById(id).getFriends();
        Set<Long> friendFriends = userStorage.getUserById(anotherId).getFriends();

        if (friendFriends.isEmpty() || userFriends.isEmpty()) {
            throw new NotFoundException("Пользователи без друзей ;(");
        }

        Collection<User> commonFrineds = new ArrayList<>();
        for (Long userFriendsdId : userFriends) {
            if (friendFriends.contains(userFriendsdId)) {
                commonFrineds.add(userStorage.getUserById(userFriendsdId));
            }
        }
        return commonFrineds;
    }

    private void friendsChecker(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.error("id пользователя или его друга не указан");
            throw new ValidationException("Id пользователя или друга должен быть указан");
        }
        if (!userStorage.getAllUsers().contains(userStorage.getUserById(id)) ||
                !userStorage.getAllUsers().contains(userStorage.getUserById(friendId))) {
            log.error("Пользователя с данным id или его друга не существует");
            throw new NotFoundException("Пользователь с данным id или его друга не найден");
        }
        if (id.equals(friendId)) {
            log.error("id пользователя и друга идентичны");
            throw new FriendForHimselfException("Пользователь не может быть сам себе другом");
        }
    }

    public Collection<User> getFriends(Long id) {
        log.info("Получение друзей пользователя");

        if (id == null) {
            throw new ValidationException("Id не указан");
        }

        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }

        Set<Long> friendIds = user.getFriends();
        if (friendIds == null || friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<User> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            User friend = userStorage.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }

        return friends;
    }


}
