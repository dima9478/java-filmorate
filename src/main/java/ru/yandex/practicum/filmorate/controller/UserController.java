package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int currentId;

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(@Valid @RequestBody User user) {
        validate(user);

        int userId = ++currentId;
        user.setId(userId);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(userId, user);

        log.debug("User {} was created {}", userId, user);

        return user;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User putUser(@Valid @RequestBody User user) {
        validate(user);
        int userId = user.getId();

        if (!users.containsKey(userId)) {
            log.debug("User with id {} wasn't updated as it can't be found in storage", userId);
            throw new NotFoundException("No user with such id " + userId);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);

        log.debug("User {} was updated: {}", user.getId(), user);

        return user;
    }

    private void validate(User user) {
        if (!UserValidator.isValid(user)) {
            log.debug("User data  is invalid: {}", user);
            throw new ValidationException("Invalid user data");
        }
    }
}
