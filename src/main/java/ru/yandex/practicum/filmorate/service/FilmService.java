package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private UserStorage userStorage;


    public Collection<Film> getPopularFilms(Long count) {
        log.info("Получение популярных фильмов");
        return filmStorage.findAll().stream().sorted((film1, film2) -> (film2.getLikes().size() - film1.getLikes().size()))
                .limit(count)
                .toList();
    }

    public void addLike(Long id, Long userId) {
        log.info("добавление лайка");
        userStorage.getUserById(userId); //внутри проверка на существование такого пользователя
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
    }

    public void deleteLike(Long id, Long userId) {
        log.info("удаление лайка");
        userStorage.getUserById(userId); //проверка на существование
        Film film = filmStorage.getFilmById(id);
        if (!film.getLikes().contains(userId)) throw new NotFoundException("Пользователь не ставил лайк фильму");
        film.getLikes().remove(userId);
    }
}
