package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Mpa> getAll() {
        String sql = "select * from mpa_rating";
        try {
            return jdbc.query(sql, MpaMapper::transformToMpa);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка получения рейтинга");
        }
    }


    @Override
    public Mpa getMpaById(int id) {
        String sqlNotfound = "SELECT COUNT(mpa_raiting_id) FROM mpa_rating";
        Integer count = jdbc.queryForObject(sqlNotfound, Integer.class);
        if (id > count || count == null) throw new NotFoundException("Такого мпа нет!");
        String sql = "select * from mpa_rating where mpa_raiting_id = ?";
        try {
            return jdbc.queryForObject(sql, MpaMapper::transformToMpa, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка получения рейтинга по id " + id);
        }
    }

    public void mpaChecker(Integer id) {
        String sql = "select * from mpa_rating where mpa_raiting_id = ?";
        try {
            jdbc.queryForObject(sql, MpaMapper::transformToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa с таким id " + id + " не сущесвует");
        }
    }
}
