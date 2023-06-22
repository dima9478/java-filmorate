package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/genres", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable("id") int genreId) {
        return genreService.getGenreById(genreId);
    }
}