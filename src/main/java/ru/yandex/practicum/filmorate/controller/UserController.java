package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Запрошен пользователь с id: {}", id);
        User user = userService.findById(id);
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрошен список всех пользователей.");
        Collection<User> users = userService.findAll();
        log.info("Найдено пользователей: {}", users.size());
        return users;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Попытка создать пользователя: {}", user);
        User createdUser = userService.create(user);
        log.info("Пользователь успешно создан: {}", createdUser);
        return createdUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Попытка обновить пользователя: {}", user);
        User updatedUser = userService.update(user);
        log.info("Пользователь успешно обновлён: {}", updatedUser);
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id={} добавляет в друзья пользователя с id={}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователь с id={} успешно добавил друга с id={}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Пользователь с id={} удаляет из друзей пользователя с id={}", id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Пользователь с id={} успешно удалил друга с id={}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.info("Запрос списка друзей пользователя с id={}", id);
        Collection<User> friends = userService.getFriends(id);
        log.info("Найдено друзей: {}", friends.size());
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос списка общих друзей пользователей с id={} и id={}", id, otherId);
        Collection<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Найдено общих друзей: {}", commonFriends.size());
        return commonFriends;
    }
}
