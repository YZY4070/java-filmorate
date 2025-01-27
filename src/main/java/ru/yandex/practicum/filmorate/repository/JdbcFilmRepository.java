package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final JdbcMpaRepository mpaRepository;

    private HashSet<Genre> getFilmGenre(Long id) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY fg.genre_id ASC";
        try {
            return new HashSet<>(Objects.requireNonNull(jdbc.query(sql, GenreMapper::transformToGenre, id)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError("Со списком жанров что-то не так");
        }
    }

    private void addGenre(Film film) {
        String deleteGenre = "DELETE FROM film_genres WHERE film_id = ?";
        String insertGenre = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        try {
            List<Genre> genreSet = film.getGenres().stream().toList();
            jdbc.update(deleteGenre, film.getId());
            jdbc.batchUpdate(insertGenre, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setLong(1, film.getId());
                    preparedStatement.setLong(2, genreSet.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genreSet.size();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка добавления жанра");
        }
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_raiting_id)" +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setString(3, film.getReleaseDate().toString());
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);
            Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(id);
            addGenre(film);
            return film;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка создания фильма");
        }
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT f.film_id, f.name as film_name, f.description, f.release_date, f.duration, f.mpa_raiting_id, " +
                "m.name AS mpa_name " +
                "FROM films f " +
                "JOIN mpa_rating m ON f.mpa_raiting_id = m.mpa_raiting_id " +
                "WHERE f.film_id = ? ";
        try {
            return jdbc.queryForObject(sql, ((resultSet, rowNum) -> {
                Film film = FilmMapper.transformToFilm(resultSet, rowNum);
                film.setGenres(getFilmGenre(id));
                film.setMpa(mpaRepository.getMpaById(film.getMpa().getId()));
                return film;
            }), id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("Фильм не найден!");
        }
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_raiting_id = ? WHERE film_id = ?";
        try {
            jdbc.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpa().getId(), film.getId());
            addGenre(film);
            return getFilmById(film.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка при добавлении фильма");
        }
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_raiting_id, " +
                "m.name AS mpa_name " +
                "FROM films f " +
                "JOIN mpa_rating m ON f.mpa_raiting_id = m.mpa_raiting_id";
        try {
            List<Film> films = jdbc.query(sql, FilmMapper::transformToFilm);
            films.forEach(f -> f.setGenres(getFilmGenre(f.getId())));
            return films;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка получения всех фильмов");
        }
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        try {
            jdbc.update(sql, userId, filmId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка при добавлении лайка");
        }
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        if (getFilmById(filmId).getLikes() == null || getFilmById(filmId).getLikes().isEmpty()) return;
        try {
            jdbc.update(sql, filmId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка при удалении лайка");
        }
    }

    public List<Film> getPopularFilms(Long count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_raiting_id, " +
                "m.name AS mpa_name, COUNT(l.film_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "JOIN mpa_rating m ON f.mpa_raiting_id = m.mpa_raiting_id " +
                "GROUP BY f.film_id, m.mpa_raiting_id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        try {
            List<Film> films = jdbc.query(sql, FilmMapper::transformToFilm, count);
            films.forEach(f -> f.setGenres(getFilmGenre(f.getId())));
            return films;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerException("Ошибка при получении популярных фильмов");
        }
    }

}
