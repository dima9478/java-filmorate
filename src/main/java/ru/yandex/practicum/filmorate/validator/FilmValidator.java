package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@UtilityClass
public class FilmValidator {
    private static final LocalDate FILM_BIRTHDAY_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public static boolean isValid(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate != null && releaseDate.isBefore(FILM_BIRTHDAY_DATE)) {
            return false;
        }
        return true;
    }
}
