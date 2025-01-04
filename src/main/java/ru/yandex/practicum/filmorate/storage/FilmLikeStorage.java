package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface FilmLikeStorage {

    boolean likeExists(Film film, User user);

    void removeLike(Film film, User user);

    void addLike(Film film, User user);

}
