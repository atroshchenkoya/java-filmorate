package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрошен список всех пользователей. Всего пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Попытка создать пользователя: {}", user);
        setNameByLoginIfNameIsNull(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Попытка обновить пользователя: {}", user);
        if (!users.containsKey(user.getId())) {
            String errorMessage = "Пользователь с id = " + user.getId() + " не найден.";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        setNameByLoginIfNameIsNull(user);
        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлён: {}", user);
        return user;
    }

    private static void setNameByLoginIfNameIsNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано. Используется логин: {}", user.getName());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
