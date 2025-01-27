package ru.yandex.practicum.filmorate.storage;

public interface FriendsStorage {
    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);
}
