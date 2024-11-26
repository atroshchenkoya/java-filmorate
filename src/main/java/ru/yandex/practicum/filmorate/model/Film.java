package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.DateValidIfNotBeforeCustomDate;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "id")
public class Film {
    private long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание фильма не может превышать 200 символов.")
    private String description;

    @DateValidIfNotBeforeCustomDate(value = "1895-12-28", message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года.")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private int duration;
}