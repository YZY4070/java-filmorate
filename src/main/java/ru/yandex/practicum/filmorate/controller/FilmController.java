package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FilmController {
    private final Map<Long, Film> filmMap = new HashMap<>();
    protected int nextId = 1;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Список фильмов");
        return filmMap.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при добавлении");
            throw new ValidationException("Неверная дата фильма!");
        }
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Начало внесение изменений в существующий фильм");
        if (film.getId() == null) {
            log.error("Ошибка при обновлении");
            throw new ValidationException("id фильма должен быть указан");
        }

        Film existingFilm = filmMap.get(film.getId());
        if (existingFilm == null) {
            log.error("Ошибка при обновлении: фильм с id {} не найден", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        existingFilm.setName(film.getName()); //обновление имени

        if (film.getReleaseDate() != null && film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) { // обновление даты выхода
            existingFilm.setReleaseDate(film.getReleaseDate());
            log.debug("Изменена дата релиза фильма с id {}", film.getId());
        }

        if (film.getDuration() != null) { // обновление длительности
            existingFilm.setDuration(film.getDuration());
            log.debug("Изменена дата релиза фильма с id {}", film.getId());
        }

        if (film.getDescription() != null && !film.getDescription().isBlank()) { // обновление описания
            existingFilm.setDescription(film.getDescription());
            log.debug("Изменена дата релиза фильма с id {}", film.getId());
        }

        log.info("Фильм с id {} успешно обновлен", film.getId());
        return existingFilm;
    }

    private long getNextId() {
        return nextId++;
    }
}
