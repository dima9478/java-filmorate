package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Validated
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film add(@Valid Film film) {
        validate(film);

        return filmStorage.create(film);
    }

    public Film update(@Valid Film film) {
        validate(film);

        return filmStorage.update(film);
    }

    private void validate(Film film) {
        if (!FilmValidator.isValid(film)) {
            log.debug("Film is invalid: {}", film);
            throw new ValidationException("Invalid film data");
        }
    }
}
