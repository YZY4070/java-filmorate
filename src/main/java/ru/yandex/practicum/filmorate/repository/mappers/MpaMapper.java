package ru.yandex.practicum.filmorate.repository.mappers;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaMapper {
    public static Mpa transformToMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_raiting_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
