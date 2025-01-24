package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserMapper;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcFriendsRepository implements FriendsStorage {
    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        try {
            jdbc.update(sql, userId, friendId);
        }catch (Exception e) {
            throw new InternalServerException("Ошибка добавления друга");
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        try {
            jdbc.update(sql, userId, friendId);
        }catch (Exception e) {
            throw new InternalServerException("Ошибка удаления друга");
        }
    }

    public List<User> getFriends(Long userId) {
        String sql = "SELECT us.* FROM users AS us" +
                "JOIN friendships as fs ON us.user_id = fs.friend_id " +
                "WHERE fs.user_id = ?";
        try {
            return jdbc.query(sql, UserMapper :: transfromToUser, userId);
        }catch (Exception e) {
            throw new InternalServerException("Ошибка при получении друзей");
        }
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        String sql = "SELECT us.* FROM users AS us" +
                "JOIN friendships AS fs1 ON us.user_id = fs1.friend_id" +
                "JOIN friendships AS fs2 ON us.user_id = fs2.friend_id" +
                "WHERE fs1.user_id = ? AND fs2.user_id = ?";
        try{
            return jdbc.query(sql, UserMapper :: transfromToUser, userId, otherUserId);
        }catch (Exception e) {
            throw new InternalServerException("Ошибка при получении общих друзей");
        }
    }
}
