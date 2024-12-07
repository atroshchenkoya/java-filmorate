package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id={} ставит лайк фильму с id={}", userId, id);
        filmService.addLike(id, userId);
        log.info("Пользователь с id={} успешно поставил лайк фильму с id={}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с id={} удаляет лайк у фильма с id={}", userId, id);
        filmService.removeLike(id, userId);
        log.info("Пользователь с id={} успешно удалил лайк у фильма с id={}", userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список популярных фильмов, количество: {}", count);
        Collection<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Найдено популярных фильмов: {}", popularFilms.size());
        return popularFilms;
    }
}
