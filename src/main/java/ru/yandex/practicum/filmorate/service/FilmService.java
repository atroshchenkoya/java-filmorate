package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film findById(Long id) {
        return getFilmOrThrow(id);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        getFilmOrThrow(film.getId());
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);  // Получаем фильм или выбрасываем исключение
        userService.findById(userId);  // Проверяем существование пользователя
        filmStorage.addLike(film, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);  // Получаем фильм или выбрасываем исключение
        userService.findById(userId);  // Проверяем существование пользователя
        filmStorage.removeLike(film, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private Film getFilmOrThrow(Long filmId) {
        return filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
    }

}
