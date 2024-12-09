package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Optional<User> findById(Long id);

    Collection<User> findAll();

    User create(User user);

    User update(User user);
}
