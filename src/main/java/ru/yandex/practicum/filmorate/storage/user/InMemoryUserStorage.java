package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        int userId = ++currentId;
        user.setId(userId);
        users.put(userId, user);

        return user;

    }

    @Override
    public User update(User user) {
        int userId = user.getId();

        if (!users.containsKey(userId)) {
            return null;
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public Collection<User> getFriends(int userId) {
        User user = users.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        return user.getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) {
        User user = users.get(userId);
        User other = users.get(otherId);
        if (user == null || other == null) {
            return new ArrayList<>();
        }

        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(other.getFriends());

        return commonFriends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(int userId, int otherId) {
        User user = users.get(userId);
        if (user != null) {
            user.addFriend(otherId);
        }
    }

    @Override
    public void deleteFriend(int userId, int otherId) {
        User user = users.get(userId);
        if (user != null) {
            user.removeFriend(otherId);
        }
    }
}
