package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable Long id) {
        log.info("Запрошен MPA рейтинг с id: {}", id);
        Mpa mpa = mpaService.findById(id);
        log.info("Найден MPA рейтинг: {}", mpa);
        return mpa;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Запрошен список всех MPA рейтингов.");
        Collection<Mpa> mpas = mpaService.findAll();
        log.info("Найдено MPA рейтингов: {}", mpas.size());
        return mpas;
    }
}
