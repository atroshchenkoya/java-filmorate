package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> findById(Long id);

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Collection<Film> getPopularFilms(int count);

}
