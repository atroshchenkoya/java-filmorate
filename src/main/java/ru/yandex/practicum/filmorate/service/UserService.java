package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден."));
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        setNameByLoginIfNameIsNull(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        checkUser(user.getId());
        setNameByLoginIfNameIsNull(user);
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public Collection<User> getFriends(Long userId) {
        User user = findById(userId);
        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = findById(userId);
        User otherUser = findById(otherUserId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = otherUser.getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::findById)
                .collect(Collectors.toList());
    }

    private void setNameByLoginIfNameIsNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано. Используется логин: {}", user.getName());
        }
    }

    public void checkUser(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
    }
}
