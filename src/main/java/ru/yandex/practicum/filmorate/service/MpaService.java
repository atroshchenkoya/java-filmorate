package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public Mpa findById(Long id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA рейтинг с id = " + id + " не найден."));
    }

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }
}
