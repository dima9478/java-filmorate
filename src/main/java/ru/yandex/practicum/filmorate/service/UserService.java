package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@Validated
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException(String.format("User with id %d not found", id));
        }
        return user;
    }

    public User add(@Valid User user) {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.create(user);
        log.debug("User {} was created", createdUser);

        return createdUser;
    }

    public User update(@Valid User user) {
        validate(user);

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        User updatedUser = userStorage.update(user);
        if (updatedUser == null) {
            int userId = user.getId();
            log.debug("User with id {} wasn't updated as it can't be found in storage", userId);
            throw new NotFoundException("No user with such id " + userId);
        }
        log.debug("User {} was updated", updatedUser);

        return updatedUser;
    }

    public void addFriendToUser(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.debug("User {} has already friend {}", user.getId(), friend.getId());
            return;
        }
        userStorage.addFriend(userId, friendId);

        log.debug("Friend {} was added to user {}", friend.getId(), user.getId());
    }

    public void deleteFriendFromUser(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            log.debug("User {} doesn't have friend {}", user.getId(), friend.getId());
            return;
        }
        userStorage.deleteFriend(userId, friendId);

        log.debug("Friend {} was removed from user {}", friend.getId(), user.getId());
    }

    public Collection<User> getUserFriends(int userId) {
        User user = getUserById(userId);

        return userStorage.getFriends(user.getId());
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        User user = getUserById(userId);
        User other = getUserById(otherId);

        return userStorage.getCommonFriends(user.getId(), other.getId());
    }

    private void validate(User user) {
        if (!UserValidator.isValid(user)) {
            log.debug("User data  is invalid: {}", user);
            throw new ValidationException("Invalid user data");
        }
    }
}
