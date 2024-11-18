package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> filmMap = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        logger.info("Список фильмов");
        return filmMap.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        logger.info("Добавление фильма");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            logger.error("Ошибка при добавлении");
            throw new ValidationException("Неверная дата фильма!");
        }
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        logger.info("Начало внесение изменений в существующий фильм");
        if (film.getId() == null) {
            logger.error("Ошибка при обновлении");
            throw new ValidationException("id фильма должен быть указан");
        }

        Film existingFilm = filmMap.get(film.getId());
        if (existingFilm == null) {
            logger.error("Ошибка при обновлении: фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        existingFilm.setName(film.getName()); //обновление имени

        if (film.getReleaseDate() != null) { // обновление даты выхода
            existingFilm.setReleaseDate(film.getReleaseDate());
            logger.trace("Изменена дата релиза фильма с id {}", film.getId());
        }

        if (film.getDuration() != null) { // обновление длительности
            existingFilm.setDuration(film.getDuration());
            logger.trace("Изменена продолжительность фильма с id {}", film.getId());
        }

        if (film.getDescription() != null && !film.getDescription().isBlank()) { // обновление описания
            existingFilm.setDescription(film.getDescription());
            logger.trace("Изменено описание фильма с id {}", film.getId());
        }

        logger.info("Фильм с id {} успешно обновлен", film.getId());
        return existingFilm;
    }

    private long getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
