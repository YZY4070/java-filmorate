package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final JdbcFilmRepository filmRepository;
    private final JdbcGenreRepository genreRepository;
    private final JdbcMpaRepository mpaRepository;
    private final JdbcUserRepository userRepository;

    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при добавлении");
            throw new ValidationException("Неверная дата фильма!");
        }
        genreRepository.genreChecker(film.getGenres());
        mpaRepository.mpaChecker(film.getMpa().getId());
        return filmRepository.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            log.error("Ошибка при обновлении");
            throw new ValidationException("id фильма должен быть указан");
        }
        Film existingFilm = getFilmById(film.getId());
        if (existingFilm == null) {
            log.error("Ошибка при обновлении: фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) { // обновление даты выхода
            log.error("Фильм не может быть до 1895");
            throw new ValidationException("Ошибка валидации фильма");
        }

        if (film.getDuration() == null) { // обновление длительности
            log.error("У фильма должна быть длительность");
            throw new ValidationException("У фильма нет длительности");
        }

        if (film.getDescription() == null || film.getDescription().isBlank()) { // обновление описания
            log.error("У фильма не должно быть пустого описания");
            throw new ValidationException("Пустое описание");
        }
        genreRepository.genreChecker(film.getGenres());
        mpaRepository.mpaChecker(film.getMpa().getId());

        return filmRepository.update(film);
    }

    public Film getFilmById(Long id) {
        return filmRepository.getFilmById(id);
    }

    public Collection<Film> getPopularFilms(Long count) {
        log.info("Получение популярных фильмов");
        return filmRepository.getPopularFilms(count);
    }

    public void addLike(Long id, Long userId) {
        log.info("добавление лайка");
        userRepository.getUserById(userId);
        filmRepository.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        log.info("удаление лайка");
        userRepository.getUserById(userId); //проверка на существование;
        filmRepository.removeLike(id, userId);
    }
}
