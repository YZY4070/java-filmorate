package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Primary
public class JdbcUserRepository implements UserStorage {
    private final JdbcTemplate jdbc;

    @Override
    public User create(User user){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into users (login, email, birthday, name) values (?, ?, ?, ?)";
        try{
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getLogin());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getBirthday().toString());
                ps.setString(4, user.getName());
                return ps;
            }, keyHolder);
            Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            user.setId(id);
            return user;
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalServerException("Ошибка при создании пользователя");
        }
    }

    @Override
    public User update(User newUser){
        String sql = "UPDATE users SET login = ?, email = ?, birthday = ?, name = ? WHERE id = ?";
        getUserById(newUser.getId());
        try{
            jdbc.update(sql, newUser.getLogin(), newUser.getEmail(), newUser.getBirthday().toString(), newUser.getName(),newUser.getId());
            return getUserById(newUser.getId());
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalServerException("Не получилось обновить пользователя");
        }
    }

    @Override
    public User getUserById(Long id){
        try {
            String sql = "select * from users where id = ?";
            return jdbc.queryForObject(sql, UserMapper::transfromToUser, id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalServerException("Пользователь с таким id не найден: " + id);
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from users";
        try{
            return jdbc.query(sql, UserMapper::transfromToUser);
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalServerException("Не получилось вернуть всех пользователей");
        }
    }
}
