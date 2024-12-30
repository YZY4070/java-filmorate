package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User update(User newUser);

    User getUserById(Long id);

    Collection<User> getAllUsers();

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long id, Long anotherId);
}
