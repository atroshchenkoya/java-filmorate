package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserFriendStorage {

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    Collection<User> getFriends(User user);
}