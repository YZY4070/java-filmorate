package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;

import java.util.Collection;

@Service
@AllArgsConstructor
public class MpaService {
    private final JdbcMpaRepository mpaRepository;

    public Collection<Mpa> getAll() {
        return mpaRepository.getAll();
    }

    public Mpa getMpaById(int id){
        return mpaRepository.getMpaById(id);
    }

    public void mpaChecker(Integer id){
        mpaRepository.mpaChecker(id);
    }
}
