package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.User;

@UtilityClass
public class UserValidator {
    public static boolean isValid(User user) {
        String login = user.getLogin();
        if (login == null || login.contains(" ")) {
            return false;
        }
        return true;
    }
}
