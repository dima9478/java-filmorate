package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId;

    @Override
    public Collection<User> getAll() {
        return users.values();
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
            log.debug("User with id {} wasn't updated as it can't be found in storage", userId);
            throw new NotFoundException("No user with such id " + userId);
        }

        users.put(user.getId(), user);

        return user;
    }
}
