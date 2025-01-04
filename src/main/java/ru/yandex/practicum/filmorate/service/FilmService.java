package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage; // Добавляем FilmLikeStorage
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
        User user = userService.findById(userId);
        Film film = findById(filmId);
        if (!filmLikeStorage.likeExists(film, user)) {
            filmLikeStorage.addLike(film, user);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        User user = userService.findById(userId);
        Film film = findById(filmId);
        if (filmLikeStorage.likeExists(film, user)) {
            filmLikeStorage.removeLike(film, user);
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void checkFilm(Long filmId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
    }
}
