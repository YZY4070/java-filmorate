package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.HashSet;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Genre> getGenres(){
        String sql = "select * from genre";
        try {
            return jdbc.query(sql, GenreMapper::transformToGenre);
        }catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка возвращения всех жанров");
        }
    }

    @Override
    public Genre getGenreById(Integer id){
        String sql = "select * from genre where id = ?";
        try{
            return jdbc.queryForObject(sql, GenreMapper::transformToGenre, id);
        }catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("Жанра с таким id не существует");
        }
    }

    public void genreChecker(HashSet<Genre> genres){
        String sql = "select * from genre";
        try {
            genres.forEach(genre -> jdbc.query(sql, GenreMapper::transformToGenre));
        }catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("?");
        }
    }
}
