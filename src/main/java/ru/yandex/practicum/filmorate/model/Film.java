package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;


import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {

    private Long id;

    @NotBlank(message = "у фильма должно быть имя")
    private String name;


    @Size(max = 200, message = "Max длина описания 200 букв")
    private String description;


    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
