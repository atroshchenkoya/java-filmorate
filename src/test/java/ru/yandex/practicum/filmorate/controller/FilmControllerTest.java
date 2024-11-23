package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private final FilmController filmController = new FilmController();

    @Test
    void shouldNotCreateFilmWithoutName() {
        Film film = new Film();
        film.setDescription("Funny movie");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));

        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));

        assertEquals("Описание фильма не может превышать 200 символов.", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Funny movie");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));

        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Funny movie");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));

        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }

    @Test
    void shouldCreateFilmSuccessfully() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmController.create(film);

        assertEquals("Movie", createdFilm.getName());
        assertEquals("A".repeat(200), createdFilm.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), createdFilm.getReleaseDate());
        assertEquals(120, createdFilm.getDuration());
    }
}
