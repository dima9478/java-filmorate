package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.Collection;

@Slf4j
@Validated
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NotFoundException(String.format("Film with id %d not found", id));
        }
        return film;
    }

    public Film add(@Valid Film film) {
        validate(film);

        Film createdFilm = filmStorage.create(film);
        log.debug("Film {} was created", createdFilm);

        return createdFilm;
    }

    public Film update(@Valid Film film) {
        validate(film);

        Film updatedFilm = filmStorage.update(film);
        if (updatedFilm == null) {
            int filmId = film.getId();
            log.debug("Film with id {} wasn't updated as it can't be found in storage", filmId);
            throw new NotFoundException("No film with such id " + filmId);
        }
        log.debug("Film {} was updated: {}", film.getId(), updatedFilm);

        return updatedFilm;
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (film.getLikes().contains(userId)) {
            log.debug("Film {} has already like from user {}", film.getId(), user.getId());
            return;
        }
        filmStorage.addLike(filmId, userId);
        log.debug("Like from user {} added to the film {}", user.getId(), film.getId());
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        if (!film.getLikes().contains(userId)) {
            log.debug("Film {} doesn't have like from user {}", film.getId(), user.getId());
            return;
        }
        filmStorage.deleteLike(filmId, userId);
        log.debug("Like from user {} deleted for the film {}", user.getId(), film.getId());
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new InvalidParameterException("Count must be positive");
        }
        return filmStorage.getPopularFilms(count);
    }

    private void validate(Film film) {
        if (!FilmValidator.isValid(film)) {
            log.debug("Film is invalid: {}", film);
            throw new ValidationException("Invalid film data");
        }
    }
}
