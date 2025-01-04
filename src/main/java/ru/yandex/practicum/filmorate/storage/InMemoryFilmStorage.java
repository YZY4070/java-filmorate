package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final UserStorage userStorage;
    private final Map<Long, Film> filmMap = new HashMap<>();
    protected int nextId = 1;

    private long getNextId() {
        return nextId++;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Список фильмов");
        return filmMap.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Добавление фильма");
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Начало внесение изменений в существующий фильм");
        Film existingFilm = filmMap.get(film.getId());
        existingFilm.setName(film.getName()); //обновление имени
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setDescription(film.getDescription());

        log.info("Фильм с id {} успешно обновлен", film.getId());
        return existingFilm;
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("получение фильма по айди");
        return filmMap.get(id);
    }

}
