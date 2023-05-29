package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User addUser(@RequestBody User user) {
        User createdUser = userService.add(user);

        log.debug("User {} was created", createdUser);

        return createdUser;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User putUser(@RequestBody User user) {
        User updatedUser = userService.update(user);

        log.debug("User {} was updated", updatedUser);

        return updatedUser;
    }
}
