package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
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
            return null;
        }
        films.put(filmId, film);

        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.addLike(userId);
        }
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.removeLike(userId);
        }
    }
}
