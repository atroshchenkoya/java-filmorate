package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_VALID_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрошен список всех фильмов. Всего фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Попытка создать фильм: {}", film);
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Попытка обновить фильм: {}", film);
        if (film.getId() == 0 || !films.containsKey(film.getId())) {
            String errorMessage = "Фильм с id = " + film.getId() + " не найден.";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлён: {}", film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            String errorMessage = "Название фильма не может быть пустым.";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMessage = "Описание фильма не может превышать 200 символов.";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_VALID_RELEASE_DATE)) {
            String errorMessage = "Дата релиза фильма не может быть раньше 28 декабря 1895 года.";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        if (film.getDuration() <= 0) {
            String errorMessage = "Продолжительность фильма должна быть положительным числом.";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
    }
}
