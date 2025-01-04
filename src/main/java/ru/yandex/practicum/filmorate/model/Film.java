package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private Long id;

    @NotBlank(message = "у фильма должно быть имя")
    private String name;


    @NotBlank
    @Size(max = 200, message = "Max длина описания 200 символов")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private Integer duration;

    private Set<Long> likes = new HashSet<>();
}
