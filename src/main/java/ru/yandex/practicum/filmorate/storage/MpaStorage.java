package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Optional<Mpa> findById(Long id);

    Collection<Mpa> findAll();
}