package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long currentMaxId = 0;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    @Override
    public Collection<User> getFriends(User user) {
        return user.getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(User user, User other) {
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    private long getNextId() {
        return ++currentMaxId;
    }

}
