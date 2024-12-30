package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.constraints.Positive;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film getFilmById(Long id);

    Film update(Film newFilm);

    Collection<Film> findAll();

    Collection<Film> getPopularFilms(@Positive Long count);

    void addLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

}
