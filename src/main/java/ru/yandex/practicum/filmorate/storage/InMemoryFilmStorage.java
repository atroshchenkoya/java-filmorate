package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 0;

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(Film film, Long userId) {
        film.getWhoLikes().add(userId);
    }

    @Override
    public void removeLike(Film film, Long userId) {
        film.getWhoLikes().remove(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getWhoLikes().size(), f1.getWhoLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return ++currentMaxId;
    }
}
