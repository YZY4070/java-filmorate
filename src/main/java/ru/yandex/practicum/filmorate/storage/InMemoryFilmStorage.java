package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NegativeCountException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component @Slf4j
public class InMemoryFilmStorage implements FilmStorage{

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
    public Collection<Film> getPopularFilms(Long count){
        if (count <= 0) throw new NegativeCountException("Число фильмов должно быть больше нуля");
        log.info("Получение популярных фильмов");
      return filmMap.values().stream().sorted((film1, film2) -> (film2.getLikes().size() - film1.getLikes().size()))
              .limit(count)
              .toList();
    }

    @Override
    public Film create(@Valid Film film) {
        log.info("Добавление фильма");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при добавлении");
            throw new ValidationException("Неверная дата фильма!");
        }
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(@Valid Film film) {
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

    @Override
    public Film getFilmById(Long id){
        log.info("получение фильма по айди");
        if (filmMap.get(id) == null) throw new NotFoundException("Такого фильма не существует");
        return filmMap.get(id);
    }

    @Override
    public void addLike(Long id, Long userId){
        log.info("добавление лайка");
        userStorage.getUserById(userId); //внутри проверка на существование такого пользователя
        Film film = getFilmById(id);
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(Long id, Long userId){
        log.info("удаление лайка");
        userStorage.getUserById(userId); //проверка на существование
        Film film = getFilmById(id);
        if(!film.getLikes().contains(userId)) throw new NotFoundException("Пользователь не ставил лайк фильму");
        film.getLikes().remove(userId);
    }
}
