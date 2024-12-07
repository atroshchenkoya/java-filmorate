package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User findById(Long id) {
        return getUserOrThrow(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        setNameByLoginIfNameIsNull(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        getUserOrThrow(user.getId());
        setNameByLoginIfNameIsNull(user);
        return userStorage.update(user);
    }

    private void setNameByLoginIfNameIsNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано. Используется логин: {}", user.getName());
        }
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        userStorage.addFriend(user, friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        userStorage.removeFriend(user, friend);
    }

    public Collection<User> getFriends(Long userId) {
        User user = getUserOrThrow(userId);
        return userStorage.getFriends(user);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);
        return userStorage.getCommonFriends(user, other);
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден."));
    }

}
