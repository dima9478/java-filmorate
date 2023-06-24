package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingStorage ratingStorage;

    public List<Rating> getAll() {
        return ratingStorage.getAll();
    }

    public Rating getRatingById(int id) {
        Rating rating = ratingStorage.getById(id);
        if (rating == null) {
            throw new NotFoundException(String.format("Rating with id %d not found", id));
        }
        return rating;
    }
}
