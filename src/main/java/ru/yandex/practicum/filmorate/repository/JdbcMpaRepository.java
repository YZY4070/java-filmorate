package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Mpa> getAll(){
        String sql = "select * from mpa";
        try{
            return jdbc.query(sql, MpaMapper::transformToMpa);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Ошибка получения рейтинга");
        }
    }

    @Override
    public Mpa getMpaById(int id){
        String sql = "select * from mpa where id = ?";
        try{
            return jdbc.queryForObject(sql, MpaMapper::transformToMpa, id);
        }catch(Exception e){
            e.printStackTrace();
            throw new InternalServerException("Ошибка получения рейтинга по id " + id);
        }
    }

    public void mpaChecker(Integer id){
        String sql = "select * from mpa where id = ?";
        try{
            jdbc.queryForObject(sql, MpaMapper::transformToMpa, id);
        }catch (Exception e){
            e.printStackTrace();
            throw new InternalServerException("?");
        }
    }
}
