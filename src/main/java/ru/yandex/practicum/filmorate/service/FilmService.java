package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден."));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        checkFilm(film.getId());
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        userService.checkUser(userId);
        Film film = findById(filmId);
        film.getWhoLikes().add(userId);
        filmStorage.update(film);
    }

    public void removeLike(Long filmId, Long userId) {
        userService.checkUser(userId);
        Film film = findById(filmId);
        film.getWhoLikes().remove(userId);
        filmStorage.update(film);
    }

    public Collection<Film> getPopularFilms(int count) {
        return findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getWhoLikes().size(), f1.getWhoLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkFilm(Long filmId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
    }
}
