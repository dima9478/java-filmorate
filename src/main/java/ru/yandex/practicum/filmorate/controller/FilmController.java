package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId;

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film addFilm(@Valid @RequestBody Film film) {
        validate(film);

        int filmId = ++currentId;
        film.setId(filmId);
        films.put(filmId, film);

        log.debug("Film {} was created {}", filmId, film);

        return film;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film putFilm(@Valid @RequestBody Film film) {
        validate(film);
        int filmId = film.getId();

        if (!films.containsKey(filmId)) {
            log.debug("Film with id {} wasn't updated as it can't be found in storage", filmId);
            throw new NotFoundException("No film with such id " + filmId);
        }

        films.put(film.getId(), film);

        log.debug("Film {} was updated: {}", film.getId(), film);
        return film;
    }

    private void validate(Film film) {
        if (!FilmValidator.isValid(film)) {
            log.debug("Film is invalid: {}", film);
            throw new ValidationException("Invalid film data");
        }
    }

}
