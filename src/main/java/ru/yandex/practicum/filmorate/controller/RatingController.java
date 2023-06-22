package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/mpa", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public List<Rating> getRatings() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable("id") int ratingId) {
        return ratingService.getRatingById(ratingId);
    }
}
