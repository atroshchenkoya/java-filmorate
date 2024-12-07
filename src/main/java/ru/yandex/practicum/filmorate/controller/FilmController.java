package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        log.info("Запрошен фильм с id: {}", id);
        Film film = filmService.findById(id);
        log.info("Найден фильм: {}", film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрошен список всех фильмов.");
        Collection<Film> films = filmService.findAll();
        log.info("Найдено фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Попытка создать фильм: {}", film);
        Film createdFilm = filmService.create(film);
        log.info("Фильм успешно создан: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Попытка обновить фильм: {}", film);
        Film updatedFilm = filmService.update(film);
        log.info("Фильм успешно обновлён: {}", updatedFilm);
        return updatedFilm;
    }
}
