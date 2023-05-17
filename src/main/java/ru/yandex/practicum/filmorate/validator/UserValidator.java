package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.User;

public class UserValidator {
    public static boolean isValid(User user) {
        if (user.getLogin().contains(" ")) {
            return false;
        }
        return true;
    }
}
