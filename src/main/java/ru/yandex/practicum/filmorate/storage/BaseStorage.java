package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface BaseStorage<T> {
    Collection<T> getAll();

    T create(T obj);

    T update(T obj);
}
