package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class GenreService {
    private final JdbcGenreRepository genreRepository;

    public Collection<Genre> getGenres() {
        return genreRepository.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreRepository.getGenreById(id);
    }
}
