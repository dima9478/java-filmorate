package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;

public interface UserStorage extends BaseStorage<User> {
    Collection<User> getFriends(int userId);

    Collection<User> getCommonFriends(int userId, int otherId);

    void addFriend(int userId, int otherId);

    void deleteFriend(int userId, int otherId);
}
