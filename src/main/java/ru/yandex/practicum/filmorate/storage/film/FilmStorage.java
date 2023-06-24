package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;

public interface FilmStorage extends BaseStorage<Film> {
    Collection<Film> getPopularFilms(int count);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
