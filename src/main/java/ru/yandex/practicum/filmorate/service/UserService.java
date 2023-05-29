package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User add(@Valid User user) {
        validate(user);

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User update(@Valid User user) {
        validate(user);

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    private void validate(User user) {
        if (!UserValidator.isValid(user)) {
            log.debug("User data  is invalid: {}", user);
            throw new ValidationException("Invalid user data");
        }
    }
}
