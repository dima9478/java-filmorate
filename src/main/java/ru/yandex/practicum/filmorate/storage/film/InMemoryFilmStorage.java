package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        int filmId = ++currentId;
        film.setId(filmId);
        films.put(filmId, film);

        return film;
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();

        if (!films.containsKey(filmId)) {
            log.debug("Film with id {} wasn't updated as it can't be found in storage", filmId);
            throw new NotFoundException("No film with such id " + filmId);
        }
        films.put(filmId, film);

        return film;
    }
}
